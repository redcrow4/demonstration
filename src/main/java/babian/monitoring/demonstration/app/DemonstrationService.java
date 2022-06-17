package babian.monitoring.demonstration.app;

import babian.monitoring.demonstration.app.dto.ClassDTO;
import babian.monitoring.demonstration.app.dto.MonitoringStatusDTO;
import babian.monitoring.demonstration.util.MonitoringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static babian.monitoring.demonstration.app.constants.CommonConstants.*;

@Service
public class DemonstrationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DemonstrationMapper demonstrationMapper;
    private final MonitoringUtil monitoringUtil;

    @Autowired
    public DemonstrationService(DemonstrationMapper demonstrationMapper, MonitoringUtil monitoringUtil) {
        this.demonstrationMapper = demonstrationMapper;
        this.monitoringUtil = monitoringUtil;
    }

    public List<ClassDTO> getClassAndBaby(int account) {
        List<ClassDTO> classDTOList = demonstrationMapper.getClassList(account);

        for (ClassDTO classDTO : classDTOList)
            classDTO.setBabyDTOList(demonstrationMapper.getBabyList(classDTO.getClassIndex()));

        return classDTOList;
    }

    public void putMonitoringStatus(String matMac, int babyIndex, int monitoringStatus) {
        if (MONITORING_OFF == monitoringStatus) {
            MONITORING_STATUS_MAP.remove(matMac);

        } else if (MONITORING_ON == monitoringStatus && !MONITORING_STATUS_MAP.containsKey(matMac)) {
            MonitoringStatusDTO monitoringStatusDTO = demonstrationMapper.getMonitoringTarget(babyIndex);
            MONITORING_STATUS_MAP.put(matMac, monitoringStatusDTO);
        }

//        logger.info("MONITORING_STATUS_MAP= {}/{}", monitoringStatus, MONITORING_STATUS_MAP);
    }

    public MonitoringStatusDTO.MonitoringStatusResponseDTO postMonitoringData(String matMac, String rawData) {
        MonitoringStatusDTO monitoringStatusDTO = getMonitoringTarget(matMac);
        MonitoringStatusDTO.MonitoringStatusResponseDTO monitoringStatusResponse = null;
        if (null != monitoringStatusDTO) {
            monitoringStatusResponse = monitoringUtil.processRawData(rawData, monitoringStatusDTO);
        }
        logger.info("{}", monitoringStatusResponse.toString());
        return monitoringStatusResponse;
    }

    private MonitoringStatusDTO getMonitoringTarget(String matMac) {
        return MONITORING_STATUS_MAP.getOrDefault(matMac, null);
    }
}