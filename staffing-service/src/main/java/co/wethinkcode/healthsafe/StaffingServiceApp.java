package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

                ctx.contentType("application/json");
                ctx.result(wardResponse.body());

            } catch (IOException | InterruptedException e) {
                ctx.status(503).result("Ward Service is unavailable");
            }
        });

        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.
    }

    private static final String WARD_SERVICE_URL = "http://localhost:7031";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

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
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
