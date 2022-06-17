package babian.monitoring.demonstration.app;

import babian.monitoring.demonstration.app.dto.BabyDTO;
import babian.monitoring.demonstration.app.dto.ClassDTO;
import babian.monitoring.demonstration.app.dto.MonitoringStatusDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DemonstrationMapper {

    // 반 조회
    List<ClassDTO> getClassList(int account);

    // 아기 조회
    List<BabyDTO> getBabyList(int classIndex);

    MonitoringStatusDTO getMonitoringTarget(int babyIndex);
}