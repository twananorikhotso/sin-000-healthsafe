package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import co.wethinkcode.healthsafe.mq.StaffingEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.JMSException;

public class StaffingServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7033);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/staffing/{wardId}", ctx -> {
            String wardId = ctx.pathParam("wardId");

            try {
                HttpResponse<String> wardResponse = getWard(wardId);

                if (wardResponse.statusCode() == 404) {
                    ctx.status(404).result("Unknown ward: " + wardId);
                    return;
                }

                if (wardResponse.statusCode() != 200) {
                    ctx.status(502).result("Ward Service returned an unexpected response");
                    return;
                }

                HttpResponse<String> alertResponse;

                try {
                    alertResponse = getAlertLevel();
                } catch (IOException | InterruptedException e) {
                    ctx.status(503).result("Alert Level Service is unavailable");
                    return;
                }

                if (alertResponse.statusCode() != 200) {
                    ctx.status(502).result("Alert Level Service returned an unexpected response");
                    return;
                }

                int alertLevel;

                try {
                    alertLevel = extractAlertLevel(alertResponse.body());
                } catch (IllegalArgumentException e) {
                    ctx.status(502).result("Alert Level Service returned invalid data");
                    return;
                }

                StaffingSchedule schedule = createSchedule(wardId, alertLevel);

                try {
                    String eventJson = OBJECT_MAPPER.writeValueAsString(schedule);
                    EVENT_PUBLISHER.publish(eventJson);
                } catch (JMSException e) {
                    ctx.status(503).result("ActiveMQ broker is unavailable");
                    return;
                }

                ctx.contentType("application/json");
                ctx.json(schedule);

            } catch (IOException | InterruptedException e) {
                ctx.status(503).result("Ward Service is unavailable");
            }
        });

        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.
    }

    private static final String WARD_SERVICE_URL = "http://localhost:7031";

    private static final String ALERT_SERVICE_URL = "http://localhost:7032";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final StaffingEventPublisher EVENT_PUBLISHER =
            new StaffingEventPublisher();

    private static HttpResponse<String> getWard(String wardId)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WARD_SERVICE_URL + "/wards/" + wardId))
                .GET()
                .build();

        return HTTP_CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private static HttpResponse<String> getAlertLevel()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ALERT_SERVICE_URL + "/alert-level"))
                .GET()
                .build();

        return HTTP_CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private static int extractAlertLevel(String responseBody) {
        String digits = responseBody.replaceAll("[^0-9]", "");

        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Invalid alert level response");
        }

        return Integer.parseInt(digits);
    }

    private static StaffingSchedule createSchedule(String wardId, int alertLevel) {

        if (alertLevel <= 2) {
            return new StaffingSchedule(
                    wardId,
                    alertLevel,
                    "NORMAL",
                    1
            );
        }

        if (alertLevel <= 5) {
            return new StaffingSchedule(
                    wardId,
                    alertLevel,
                    "ELEVATED",
                    2
            );
        }

        if (alertLevel <= 7) {
            return new StaffingSchedule(
                    wardId,
                    alertLevel,
                    "HIGH",
                    3
            );
        }

        return new StaffingSchedule(
                wardId,
                alertLevel,
                "CODE_BLUE",
                4
        );
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
