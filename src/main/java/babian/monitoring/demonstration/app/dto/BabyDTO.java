package babian.monitoring.demonstration.app.dto;

public class BabyDTO {
    private final int babyIndex;
    private final int classIndex;
    private final String babyName;
    private final int monitoringStatus;
    private final String matMac;

    public BabyDTO(int babyIndex, int classIndex, String babyName, int monitoringStatus, String matMac) {
        this.babyIndex = babyIndex;
        this.classIndex = classIndex;
        this.babyName = babyName;
        this.monitoringStatus = monitoringStatus;
        this.matMac = matMac;
    }

    public BabyDTO(int babyIndex, int classIndex, String babyName) {
        this.babyIndex = babyIndex;
        this.classIndex = classIndex;
        this.babyName = babyName;
        this.monitoringStatus = 0;
        this.matMac = "";
    }

    public int getBabyIndex() {
        return babyIndex;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public String getBabyName() {
        return babyName;
    }

    public int getMonitoringStatus() {
        return monitoringStatus;
    }

    public String getMatMac() {
        return matMac;
    }
}