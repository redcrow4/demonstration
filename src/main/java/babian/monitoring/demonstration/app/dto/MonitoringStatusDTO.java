package babian.monitoring.demonstration.app.dto;

import java.util.LinkedList;

import static babian.monitoring.demonstration.app.constants.CommonConstants.*;

public class MonitoringStatusDTO {
    private final int classIndex;
    private final String className;
    private final int babyIndex;
    private final String babyName;
    private final String matMac;

    public int postureStatusGoodCnt = 0;
    public int postureStatusLeftCnt = 0;
    public int postureStatusRightCnt = 0;
    public int postureStatusReverseCnt = 0;
    public int postureStatusUnknownCnt = 0;
    public int postureContinuousEmergencyCnt = 0;

    public int breathStatusGoodCnt = 0;
    public int breathStatusLowCnt = 0;
    public int breathStatusEmergencyCnt = 0;
    public int breathStatusUnknownCnt = 0;
    public int breathContinuousEmergencyCnt = 0;

    // 자세/호흡 계산용
    public int postureSumValue = VALUE_ZERO; // 자세 9초에 1번씩(3번 쌓이면 계산시작, 최대 10번(30초) 까지 쌓아서 계산) 들어온 횟수
    public int prevTimeStep = VALUE_ZERO; //
    public LinkedList<Double> pressureData = new LinkedList<>(); // 로우데이터 10회분 (30초)
    public int postureValue;    // 자세상태
    public int breathRateValue; // 호흡상태

    // 현재 자세/호흡 결과 값
    public int postureStatus = STATE_GOOD;
    public int breathStatus = STATE_GOOD;

    // 이전 자세/호흡 결과 값
    public int prevPostureStatus = STATE_GOOD;
    public int prevBreathStatus = STATE_GOOD;
    public int babyState = STATE_GOOD;     // 현재 아기 상태 응급/위험/좋음

    public int postureNotCenterCnt = VALUE_ZERO;     // 자세 좌/우
    public int postureReverseCnt = VALUE_ZERO;     // 자세 뒤집힘
    public int postureUnknownCnt = VALUE_ZERO;     // 자세 측정불가

    public int breathLowCnt = VALUE_ZERO;     // 호흡 낮음
    public int breathVeryLowCnt = VALUE_ZERO;     // 호흡 매우 낮음

    public boolean isSendNotifications = false;          // 알람 발송여부
    public String notificationCode = NOTIFY_CODE_GOOD; // 알람 발송 코드

    public MonitoringStatusDTO(int classIndex, String className, int babyIndex, String babyName, String matMac) {
        this.classIndex = classIndex;
        this.className = className;
        this.babyIndex = babyIndex;
        this.babyName = babyName;
        this.matMac = matMac;
    }

    public MonitoringStatusDTO(int classIndex, String className, int babyIndex, String babyName) {
        this.classIndex = classIndex;
        this.className = className;
        this.babyIndex = babyIndex;
        this.babyName = babyName;
        this.matMac = "";
    }

    @Override
    public String toString() {
        return "MonitoringStatusDTO{" +
                "classIndex=" + classIndex +
                ", className='" + className + '\'' +
                ", babyIndex=" + babyIndex +
                ", babyName='" + babyName + '\'' +
                ", matMac='" + matMac + '\'' +
                ", postureStatusGoodCnt=" + postureStatusGoodCnt +
                ", postureStatusLeftCnt=" + postureStatusLeftCnt +
                ", postureStatusRightCnt=" + postureStatusRightCnt +
                ", postureStatusReverseCnt=" + postureStatusReverseCnt +
                ", postureStatusUnknownCnt=" + postureStatusUnknownCnt +
                ", postureContinuousEmergencyCnt=" + postureContinuousEmergencyCnt +
                ", breathStatusGoodCnt=" + breathStatusGoodCnt +
                ", breathStatusLowCnt=" + breathStatusLowCnt +
                ", breathStatusEmergencyCnt=" + breathStatusEmergencyCnt +
                ", breathStatusUnknownCnt=" + breathStatusUnknownCnt +
                ", breathContinuousEmergencyCnt=" + breathContinuousEmergencyCnt +
                ", postureSumValue=" + postureSumValue +
                ", prevTimeStep=" + prevTimeStep +
                ", pressureData=" + pressureData +
                ", postureValue=" + postureValue +
                ", breathRateValue=" + breathRateValue +
                ", postureStatus=" + postureStatus +
                ", breathStatus=" + breathStatus +
                ", prevPostureStatus=" + prevPostureStatus +
                ", prevBreathStatus=" + prevBreathStatus +
                ", babyState=" + babyState +
                ", postureNotCenterCnt=" + postureNotCenterCnt +
                ", postureReverseCnt=" + postureReverseCnt +
                ", postureUnknownCnt=" + postureUnknownCnt +
                ", breathLowCnt=" + breathLowCnt +
                ", breathVeryLowCnt=" + breathVeryLowCnt +
                ", isSendNotifications=" + isSendNotifications +
                ", notificationCode='" + notificationCode + '\'' +
                '}';
    }

    public String toStringSimple() {
        return "MonitoringStatusDTO{" +
                "classIndex=" + classIndex +
                ", className='" + className + '\'' +
                ", babyIndex=" + babyIndex +
                ", babyName='" + babyName + '\'' +
                ", matMac='" + matMac + '\'' +
                ", postureStatus=" + postureStatus +
                ", breathStatus=" + breathStatus +
                ", isSendNotifications=" + isSendNotifications +
                ", notificationCode='" + notificationCode + '\'' +
                '}';
    }

    public void initCnt() {
        this.postureStatusGoodCnt = 0;
        this.postureStatusLeftCnt = 0;
        this.postureStatusRightCnt = 0;
        this.postureStatusReverseCnt = 0;
        this.postureStatusUnknownCnt = 0;
        this.postureContinuousEmergencyCnt = 0;
        this.breathStatusGoodCnt = 0;
        this.breathStatusLowCnt = 0;
        this.breathStatusEmergencyCnt = 0;
        this.breathStatusUnknownCnt = 0;
        this.breathContinuousEmergencyCnt = 0;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public String getClassName() {
        return className;
    }

    public int getBabyIndex() {
        return babyIndex;
    }

    public String getBabyName() {
        return babyName;
    }

    public String getMatMac() {
        return matMac;
    }

    public static class MonitoringStatusResponseDTO {
        private final int babyIndex; // 아기 인덱스
        private final int babyState; // 아기 상태 응급/위험/좋음
        private final int postureValue; // 자세 상태
        private final int breathRateValue; // 호흡 상태
        private final boolean isSendNotifications; // 알람 발송여부
        private final String notificationCode; // 알람 발송 코드

        public MonitoringStatusResponseDTO(int babyIndex, int babyState, int postureValue, int breathRateValue, boolean isSendNotifications, String notificationCode) {
            this.babyIndex = babyIndex;
            this.babyState = babyState;
            this.postureValue = postureValue;
            this.breathRateValue = breathRateValue;
            this.isSendNotifications = isSendNotifications;
            this.notificationCode = notificationCode;
        }

        @Override
        public String toString() {
            return "MonitoringStatusResponseDTO{" +
                    "babyIndex=" + babyIndex +
                    ", babyState=" + babyState +
                    ", postureValue=" + postureValue +
                    ", breathRateValue=" + breathRateValue +
                    ", isSendNotifications=" + isSendNotifications +
                    ", notificationCode='" + notificationCode + '\'' +
                    '}';
        }

        public int getBabyIndex() {
            return babyIndex;
        }

        public int getBabyState() {
            return babyState;
        }

        public int getPostureValue() {
            return postureValue;
        }

        public int getBreathRateValue() {
            return breathRateValue;
        }

        public boolean isSendNotifications() {
            return isSendNotifications;
        }

        public String getNotificationCode() {
            return notificationCode;
        }
    }
}