package co.wethinkcode.healthsafe;

public class Ward {

    private String wardId;
    private String wing;
    private String department;
    private String bedsAvailable;

    public Ward() {
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBedsAvailable() {
        return bedsAvailable;
    }

    public void setBedsAvailable(String bedsAvailable) {
        this.bedsAvailable = bedsAvailable;
    }
}