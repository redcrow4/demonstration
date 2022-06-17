package babian.monitoring.demonstration.app;

import babian.monitoring.demonstration.app.dto.ClassDTO;
import babian.monitoring.demonstration.app.dto.MonitoringStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DemonstrationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DemonstrationService demonstrationService;

    @Autowired
    public DemonstrationController(DemonstrationService demonstrationService) {
        this.demonstrationService = demonstrationService;
    }

    @GetMapping("/class-baby/{account}")
    public List<ClassDTO> getClassAndBaby(@PathVariable int account) {
        return demonstrationService.getClassAndBaby(account);
    }

    @PutMapping("/monitoring-status")
    public void putMonitoringStatus(@RequestBody Map<String, String> monitoringStatusMap) {
        demonstrationService.putMonitoringStatus(monitoringStatusMap.get("matMac")
                , Integer.parseInt(monitoringStatusMap.get("babyIndex"))
                , Integer.parseInt(monitoringStatusMap.get("monitoringStatus")));
    }

    @PostMapping("/monitoring-data")
    public MonitoringStatusDTO.MonitoringStatusResponseDTO postMonitoringData(@RequestBody Map<String, String> monitoringDataMap) {
        logger.info("postMonitoringData= {}", monitoringDataMap);
        return demonstrationService.postMonitoringData(monitoringDataMap.get("matMac")
                , monitoringDataMap.get("rawData")
        );
    }

}