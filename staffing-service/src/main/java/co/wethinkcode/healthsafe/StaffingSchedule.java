package co.wethinkcode.healthsafe;

public class StaffingSchedule {

    private final String wardId;
    private final int alertLevel;
    private final String status;
    private final int doctorsOnCall;

    public StaffingSchedule(
            String wardId,
            int alertLevel,
            String status,
            int doctorsOnCall
    ) {
        this.wardId = wardId;
        this.alertLevel = alertLevel;
        this.status = status;
        this.doctorsOnCall = doctorsOnCall;
    }

    public String getWardId() {
        return wardId;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public String getStatus() {
        return status;
    }

    public int getDoctorsOnCall() {
        return doctorsOnCall;
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