package babian.monitoring.demonstration.app.dto;

import java.util.List;

public class ClassDTO {

    private final int classIndex;
    private final String className;
    private List<BabyDTO> babyDTOList;

    public ClassDTO(int classIndex, String className) {
        this.classIndex = classIndex;
        this.className = className;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public String getClassName() {
        return className;
    }

    public List<BabyDTO> getBabyDTOList() {
        return babyDTOList;
    }

    public void setBabyDTOList(List<BabyDTO> babyDTOList) {
        this.babyDTOList = babyDTOList;
    }

    public void addBabyDTOList(BabyDTO babyDTO) {
        this.babyDTOList.add(babyDTO);
    }
}