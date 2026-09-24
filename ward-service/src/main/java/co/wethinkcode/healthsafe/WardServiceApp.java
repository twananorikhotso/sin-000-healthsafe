package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.wethinkcode.healthsafe.mq.StaffingEventSubscriber;
import co.wethinkcode.healthsafe.mq.EquipmentFailurePublisher;
import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class WardServiceApp {

    private static final String INGESTION_URL = "http://localhost:7030/wards";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        StaffingEventSubscriber staffingEventSubscriber =
                new StaffingEventSubscriber();

        staffingEventSubscriber.start();

        EquipmentFailurePublisher equipmentFailurePublisher =
                new EquipmentFailurePublisher();

        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> {
            List<Ward> wards = fetchWards();
            ctx.json(wards);
        });

        app.get("/wards/{id}", ctx -> {
            String wardId = ctx.pathParam("id");

            List<Ward> wards = fetchWards();

            Ward ward = wards.stream()
                    .filter(item -> item.getWardId().equalsIgnoreCase(wardId))
                    .findFirst()
                    .orElse(null);

            if (ward == null) {
                ctx.status(404).result("Ward not found");
                return;
            }

            ctx.json(ward);
        });

        app.post("/wards/{id}/equipment-failures", ctx -> {
            String wardId = ctx.pathParam("id");

            List<Ward> wards = fetchWards();

            Ward ward = wards.stream()
                    .filter(item -> item.getWardId().equalsIgnoreCase(wardId))
                    .findFirst()
                    .orElse(null);

            if (ward == null) {
                ctx.status(404).result("Ward not found");
                return;
            }

            EquipmentFailureRequest request;

            try {
                request = objectMapper.readValue(
                        ctx.body(),
                        EquipmentFailureRequest.class
                );
            } catch (Exception e) {
                ctx.status(400).result("Invalid equipment failure request");
                return;
            }

            if (request.equipment() == null
                    || request.equipment().isBlank()) {
                ctx.status(400).result("Equipment is required");
                return;
            }

            EquipmentFailureEvent event =
                    new EquipmentFailureEvent(
                            ward.getWardId(),
                            request.equipment().trim(),
                            "FAILED"
                    );

            try {
                String eventJson =
                        objectMapper.writeValueAsString(event);

                equipmentFailurePublisher.publish(eventJson);

                ctx.status(202).json(event);

            } catch (Exception e) {
                System.err.println(
                        "Unable to publish equipment failure: "
                                + e.getMessage()
                );

                ctx.status(503)
                        .result("Unable to publish equipment failure");
            }
        });

        app.get("/departments", ctx -> {
            List<Ward> wards = fetchWards();

            List<String> departments = wards.stream()
                    .map(Ward::getDepartment)
                    .distinct()
                    .toList();

            ctx.json(departments);
        });
    }

    private static List<Ward> fetchWards() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INGESTION_URL))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {
                System.err.println(
                        "Ingestion service returned status: "
                                + response.statusCode()
                );

                return Collections.emptyList();
            }

            return objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<Ward>>() {}
            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to fetch wards from ingestion service: "
                            + e.getMessage()
            );

            return Collections.emptyList();
        }
    }

    public record EquipmentFailureRequest(String equipment) {
    }

    public record EquipmentFailureEvent(
            String wardId,
            String equipment,
            String status
    ) {
    }
}

// TODO (Provides lists of wards and departments.)
// Add domain endpoints for ward-service here.

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
