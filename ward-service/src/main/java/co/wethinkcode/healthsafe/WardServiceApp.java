package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> {
            List<Ward> wards = fetchWards();
            ctx.json(wards);
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
}

// TODO (Provides lists of wards and departments.)
// Add domain endpoints for ward-service here.

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
