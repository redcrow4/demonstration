package midashnt.babymonitoring.iot.Constants;

import midashnt.babymonitoring.iot.TestManager.DTO.SensorDataDTO;

// 자세 및 호흡 계산에 사용 되는 상수들
public class RawDataProcessorConstants {

    public static int POSTURE_UNKNOWN_THRESHOLD     = 4;    // 4 미만값 측정 불가
    public static int LOWER_BOUND_LENGTH            = 2;    // 최소 호흡 계산 길이 6초 (=2*3)
    public static int UPPER_BOUND_LENGTH            = 9;    // 최대 호흡 계산 길이 27초 (=9*3)
    public static double CUT_OFF_FREQUENCY          = 1.33; // lpf cut-off frequency (Hz), 약 80회/분 이상 필터링
    public static int WINDOW_SIZE                   = 12;   // moving average filter 윈도우 크기 ~= 8 * 1.5초

    public static int BREATH_RR_EMERGENCY           = 5;    // RR이 5보다 작으면 응급
    public static int BREATH_RR_LOW                 = 10;   // RR이 5이상 10보다 작으면 저호흡
    public static int BREATH_RR_GOOD                = 50;   // RR이 10이상 50보다 작으면 정상, 50이상은 응급

    public static void applySensorData(SensorDataDTO sensorDataDTO) {
        POSTURE_UNKNOWN_THRESHOLD = sensorDataDTO.getPostureUnknownThreshold();
        LOWER_BOUND_LENGTH        = sensorDataDTO.getLowerBoundLength();
        UPPER_BOUND_LENGTH        = sensorDataDTO.getUpperBoundLength();
        CUT_OFF_FREQUENCY         = sensorDataDTO.getCutOffFrequency();
        WINDOW_SIZE               = sensorDataDTO.getWindowSize();
        BREATH_RR_EMERGENCY       = sensorDataDTO.getBreathRrEmergency();
        BREATH_RR_LOW             = sensorDataDTO.getBreathRrLow();
        BREATH_RR_GOOD            = sensorDataDTO.getBreathRrGood();
    }

}