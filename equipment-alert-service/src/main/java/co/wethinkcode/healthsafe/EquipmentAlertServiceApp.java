package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import co.wethinkcode.healthsafe.mq.EquipmentFailureSubscriber;

public class EquipmentAlertServiceApp {

    public static void main(String[] args) {
        EquipmentFailureSubscriber equipmentFailureSubscriber =
                new EquipmentFailureSubscriber();

        equipmentFailureSubscriber.start();

        Javalin app = Javalin.create().start(7034);

        app.get("/health", ctx -> ctx.result("OK"));
    }
}
