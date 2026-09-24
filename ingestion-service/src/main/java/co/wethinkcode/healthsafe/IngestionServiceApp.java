package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.io.IOException;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) throws IOException {
        Javalin app = Javalin.create().start(7030);  // creates a web server using Javalin

        WardCsvReader reader = new WardCsvReader();
        List<Ward> wards = reader.readWards();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> ctx.json(wards));


    }
}
