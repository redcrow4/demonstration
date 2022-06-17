package babian.monitoring.demonstration.util;

import babian.monitoring.demonstration.app.dto.MonitoringStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static babian.monitoring.demonstration.app.constants.CommonConstants.*;

@Component
public class MonitoringUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RawDataProcessor rawDataProcessor;

    @Autowired
    public MonitoringUtil(RawDataProcessor rawDataProcessor) {
        this.rawDataProcessor = rawDataProcessor;
    }

    public MonitoringStatusDTO.MonitoringStatusResponseDTO processRawData(String rawData, MonitoringStatusDTO monitoringStatusDTO) {
//        logger.info("postMonitoringData= {}", rawData);
        int timeStep = getTimeStep(rawData);
        rawData = rawData.substring(2);
//        logger.info("processRawData result = {} / {}", timeStep, rawData);
        monitoringStatusDTO = rawDataProcessor.getPressData(timeStep, rawData, monitoringStatusDTO); // 신규로직
        monitoringStatusDTO = babyConditionJudgment(monitoringStatusDTO);
        monitoringStatusDTO = babyStatusCount(monitoringStatusDTO);
//        logger.info("monitoringStatusDTO= {}", monitoringStatusDTO.toString());
        return setResult(monitoringStatusDTO);
    }

    public MonitoringStatusDTO.MonitoringStatusResponseDTO setResult(MonitoringStatusDTO monitoringStatusDTO) {
        return new MonitoringStatusDTO.MonitoringStatusResponseDTO(
                monitoringStatusDTO.getBabyIndex(),
                monitoringStatusDTO.babyState,
                monitoringStatusDTO.postureStatus,
                monitoringStatusDTO.breathStatus,
                monitoringStatusDTO.isSendNotifications,
                monitoringStatusDTO.notificationCode
        );
    }

    private int getTimeStep(String rawData) {
        return Integer.parseInt(rawData.substring(0, rawData.length() % 120), 16);
    }

    // 아기 상태 판단
    public MonitoringStatusDTO babyConditionJudgment(MonitoringStatusDTO monitoringStatusDTO) {
        final int postureCode   = monitoringStatusDTO.postureStatus;
        final int breathCode    = monitoringStatusDTO.breathStatus;
        final int prevBabyState = monitoringStatusDTO.babyState;

        String notificationCode = NOTIFY_CODE_GOOD;
        int babyState = STATE_GOOD;
        boolean isSendNotifications = false;

        // 자세 카운트
        int postureNotCenterCnt = monitoringStatusDTO.postureNotCenterCnt; // 자세 좌/우
        int postureReverseCnt   = monitoringStatusDTO.postureReverseCnt; // 자세 뒤집힘
        int postureUnknownCnt   = monitoringStatusDTO.postureUnknownCnt; // 자세 측정불가

        // 호흡 카운트
        int breathLowCnt     = monitoringStatusDTO.breathLowCnt; // 호흡 낮음
        int breathVeryLowCnt = monitoringStatusDTO.breathVeryLowCnt; // 호흡 매우 낮음

        // 자세 2:정상
        if (POSTURE_STATUS_GOOD == postureCode) {
            postureNotCenterCnt = 0; // 좌/우    카운트 초기화
            postureReverseCnt   = 0; // 뒤집힘   카운트 초기화
            postureUnknownCnt   = 0; // 측정불가 카운트 초기화

            // 호흡 2:정상 || -1:계산 중
            if (BREATH_STATUS_GOOD == breathCode || BREATH_STATUS_NOT_READY == breathCode) {
                notificationCode = NOTIFY_CODE_GOOD; // 자세:2 호흡:2 or 자세:2 호흡:5
                breathLowCnt     = 0; // 호흡 낮음      카운트 초기화
                breathVeryLowCnt = 0; // 호흡 매우 낮음 카운트 초기화

                // 호흡 3:호흡 낮음
            } else if (BREATH_STATUS_LOW == breathCode) {
                breathLowCnt++; // 호흡 낮음      카운트 증가
                breathVeryLowCnt = 0; // 호흡 매우 낮음 카운트 초기화

                // 호흡 낮음 15초 경과 && 이전상태 응급아님
                if (DELAY_TIME_15 == breathLowCnt && STATE_EMERGENCY != prevBabyState) { // 15초
                    notificationCode = NOTIFY_CODE_BREATH_LOW_DANGER; // 자세:2 호흡:3
                    babyState = STATE_DANGER;
                    isSendNotifications = true; // 알람발송 함

                    // 호흡 낮음 21초 경과 마다
                } else if (breathLowCnt % 7 == 0) { // 21초
                    notificationCode = NOTIFY_CODE_BREATH_LOW_EMERGENCY; // 자세:2 호흡:3
                    babyState = STATE_EMERGENCY;
                    isSendNotifications = true; // 알람발송 함

                } else
                    babyState = prevBabyState; // 기존상태 유지

                // 호흡 4:호흡 매우 낮음(21초 경과시 마다)
            } else if (BREATH_STATUS_EMERGENCY == breathCode) {
                breathLowCnt = 0; // 호흡 낮음      카운트 초기화
                breathVeryLowCnt++; // 호흡 매우 낮음 카운트 증가

                if (breathVeryLowCnt == 1 || breathVeryLowCnt % 7 == 0) {
                    notificationCode = NOTIFY_CODE_BREATH_EMERGENCY; // 자세:2 호흡:4
                    babyState = STATE_EMERGENCY;
                    isSendNotifications = true; // 알람발송 함

                } else
                    babyState = prevBabyState; // 기존상태 유지

            } else {
                babyState = prevBabyState; // 기존상태 유지
            }

            // 자세 3:왼쪽 || 자세 4:오른쪽
        } else if (POSTURE_STATUS_LEFT == postureCode || POSTURE_STATUS_RIGHT == postureCode) {
            postureNotCenterCnt++; // 좌/우    카운트 증가
            postureReverseCnt = 0; // 뒤집힘   카운트 초기화
            postureUnknownCnt = 0; // 측정불가 카운트 초기화

            breathLowCnt     = 0; // 호흡 낮음      초기화
            breathVeryLowCnt = 0; // 호흡 매우 낮음 초기화

            // 좌/우 카운트 == 1 && 이전자세 좌/우 아님
            if (DELAY_TIME_03 == postureNotCenterCnt && monitoringStatusDTO.prevPostureStatus != POSTURE_STATUS_LEFT && monitoringStatusDTO.prevPostureStatus != POSTURE_STATUS_RIGHT) {
                if (POSTURE_STATUS_LEFT == postureCode) // 좌/우 구분
                    notificationCode = NOTIFY_CODE_POSTURE_LEFT_DANGER; // 자세:3 호흡:5
                else
                    notificationCode = NOTIFY_CODE_POSTURE_RIGHT_DANGER; // 자세:4 호흡:5

                babyState = STATE_DANGER;
                isSendNotifications = true; // 알람발송 함

                // 좌/우 카운트 == 4 && 이전 상태 위험 || 21초 경과시 마다
            } else if ((DELAY_TIME_12 == postureNotCenterCnt && STATE_DANGER == prevBabyState) || (postureNotCenterCnt - DELAY_TIME_12) % 7 == 0) {
                if (POSTURE_STATUS_LEFT == postureCode) // 좌/우 구분
                    notificationCode = NOTIFY_CODE_POSTURE_LEFT_EMERGENCY; // 자세:3 호흡:5
                else
                    notificationCode = NOTIFY_CODE_POSTURE_RIGHT_EMERGENCY; // 자세:4 호흡:5

                babyState = STATE_EMERGENCY;
                isSendNotifications = true; // 알람발송 함

            } else
                babyState = prevBabyState; // 기존상태 유지

            // 자세 5:뒤집힘
        } else if (POSTURE_STATUS_ROLLOVER == postureCode) {
            postureNotCenterCnt = 0; // 좌/우    카운트 초기화
            postureReverseCnt++; // 뒤집힘   카운트 증가
            postureUnknownCnt = 0; // 측정불가 카운트 초기화

            breathLowCnt     = 0; // 호흡 낮음      초기화
            breathVeryLowCnt = 0; // 호흡 매우 낮음 초기화

            // 최초 발생하거나 발생 후 21초 경과시 마다
            if (postureReverseCnt == 1 || postureReverseCnt % 7 == 0) {
                notificationCode = NOTIFY_CODE_POSTURE_ROLLOVER_EMERGENCY; // 자세:5 호흡:5
                babyState = STATE_EMERGENCY;
                isSendNotifications = true; // 알람발송 함

            } else
                babyState = prevBabyState; // 기존상태 유지

            // 자세상태 6:측정불가
        } else if (POSTURE_STATUS_UNKNOWN == postureCode) {
            postureNotCenterCnt = 0; // 좌/우  카운트 초기화
            postureReverseCnt   = 0; // 뒤집힘 카운트 초기화
            postureUnknownCnt++    ; // 측정불가 카운트 증가

            breathLowCnt     = 0; // 호흡 낮음      초기화
            breathVeryLowCnt = 0; // 호흡 매우 낮음 초기화

            // 측정불가 15초 경과 -> 위험
            if (DELAY_TIME_15 == postureUnknownCnt && STATE_EMERGENCY != prevBabyState) { // 15초
                notificationCode = NOTIFY_CODE_POSTURE_UNKNOWN_DANGER; // 자세:6 호흡:5
                babyState = STATE_DANGER;
                isSendNotifications = true; // 알람발송 함

                // 측정불가 21초 유지시 마다 -> 응급
            } else if (DELAY_TIME_21 <= postureUnknownCnt && postureUnknownCnt % 7 == 0) { // 21초
                notificationCode = NOTIFY_CODE_POSTURE_UNKNOWN_EMERGENCY; // 자세:6 호흡:5
                babyState = STATE_EMERGENCY;
                isSendNotifications = true; // 알람발송 함

            } else
                babyState = prevBabyState; // 기존상태 유지

        } else {
            babyState = prevBabyState; // 기존상태 유지
        }

        // 값 갱신
        monitoringStatusDTO.isSendNotifications = isSendNotifications; // 알람 발송여부
        monitoringStatusDTO.notificationCode    = notificationCode; // 알람 발송 코드
        monitoringStatusDTO.babyState           = babyState; // 현재 아기 상태 응급/위험/좋음

        monitoringStatusDTO.postureNotCenterCnt = postureNotCenterCnt; // 자세 좌/우 카운트
        monitoringStatusDTO.postureReverseCnt   = postureReverseCnt; // 자세 뒤집힘 카운트
        monitoringStatusDTO.postureUnknownCnt   = postureUnknownCnt; // 자세 측정불가 카운트

        monitoringStatusDTO.breathLowCnt     = breathLowCnt; // 호흡 낮음 카운트
        monitoringStatusDTO.breathVeryLowCnt = breathVeryLowCnt; // 호흡 매우 낮음 카운트

        return monitoringStatusDTO;
    }

    // 5분간 아기 상태코드 카운트
    public MonitoringStatusDTO babyStatusCount(MonitoringStatusDTO monitoringStatus) {
        int postureStatus = monitoringStatus.postureStatus;
        int breathStatus  = monitoringStatus.breathStatus;

        // 자세 연속응급 카운트 5미만
        if (monitoringStatus.breathContinuousEmergencyCnt < COUNT_FIV) {

            if (POSTURE_STATUS_GOOD == postureStatus) { // 자세 2:좋음
                monitoringStatus.postureStatusGoodCnt++;
                monitoringStatus.postureContinuousEmergencyCnt = VALUE_ZERO;

            } else if (POSTURE_STATUS_LEFT == postureStatus) {
                monitoringStatus.postureStatusLeftCnt++;
                monitoringStatus.breathStatusUnknownCnt++;
                monitoringStatus.postureContinuousEmergencyCnt = VALUE_ZERO;

            } else if (POSTURE_STATUS_RIGHT == postureStatus) {
                monitoringStatus.postureStatusRightCnt++;
                monitoringStatus.breathStatusUnknownCnt++;
                monitoringStatus.postureContinuousEmergencyCnt = VALUE_ZERO;

            } else if (POSTURE_STATUS_ROLLOVER == postureStatus) {
                monitoringStatus.postureStatusReverseCnt++;
                monitoringStatus.breathStatusUnknownCnt++;
                monitoringStatus.postureContinuousEmergencyCnt++;

            } else {
                monitoringStatus.postureStatusUnknownCnt++;
                monitoringStatus.breathStatusUnknownCnt++;
                monitoringStatus.postureContinuousEmergencyCnt = VALUE_ZERO;
            }
        }

        // 자세상태 2:좋음 && 호흡 연속응급 카운트 3미만
        if (POSTURE_STATUS_GOOD == postureStatus && monitoringStatus.breathContinuousEmergencyCnt < COUNT_THR) {
            if (BREATH_STATUS_GOOD == breathStatus) {
                monitoringStatus.breathStatusGoodCnt++;
                monitoringStatus.breathContinuousEmergencyCnt = VALUE_ZERO;

            } else if (BREATH_STATUS_LOW == breathStatus) {
                monitoringStatus.breathStatusLowCnt++;
                monitoringStatus.breathContinuousEmergencyCnt = VALUE_ZERO;

            } else if (BREATH_STATUS_EMERGENCY == breathStatus) {
                monitoringStatus.breathStatusEmergencyCnt++;
                monitoringStatus.breathContinuousEmergencyCnt++;

            } else {
                monitoringStatus.breathStatusUnknownCnt++;
                monitoringStatus.breathContinuousEmergencyCnt = 0;
            }

        } else if (POSTURE_STATUS_GOOD != postureStatus) { // 자세상태 2:좋음 아님
            monitoringStatus.breathStatusUnknownCnt++;
            monitoringStatus.breathContinuousEmergencyCnt = 0;
        }

        return monitoringStatus;
    }

}