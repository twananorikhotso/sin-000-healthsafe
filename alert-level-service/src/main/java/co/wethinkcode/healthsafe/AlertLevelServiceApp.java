package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

public class AlertLevelServiceApp {

    private static int currentAlertLevel = 0;

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/alert-level", ctx -> {
            ctx.json(new AlertLevelResponse(currentAlertLevel));
        });

        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }

    public record AlertLevelResponse(int level) {
    }
}
