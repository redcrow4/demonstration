package babian.monitoring.demonstration.app.constants;

import babian.monitoring.demonstration.app.dto.MonitoringStatusDTO;

import java.util.HashMap;
import java.util.Map;

public class CommonConstants {

    public static final int MONITORING_ON  = 1;
    public static final int MONITORING_OFF = 0;

    public static final Map<String, MonitoringStatusDTO> MONITORING_STATUS_MAP = new HashMap<>();

    public static final int VALUE_ZERO = 0;
    public static final int VALUE_ONE  = 1;

    // 알람발송 지연 시간 정의(1 = 3초)
    public static int DELAY_TIME_03 = 1; //  3초
    public static int DELAY_TIME_06 = 2; //  6초
    public static int DELAY_TIME_12 = 4; // 12초
    public static int DELAY_TIME_15 = 5; // 15초
    public static int DELAY_TIME_21 = 7; // 21초

    // 알람발송 코드 정의
    public static String NOTIFY_CODE_GOOD                       = "0000";
    public static String NOTIFY_CODE_BREATH_LOW_DANGER          = "2100"; // 자세:2 호흡:3
    public static String NOTIFY_CODE_BREATH_LOW_EMERGENCY       = "2200"; // 자세:2 호흡:3
    public static String NOTIFY_CODE_BREATH_EMERGENCY           = "2201"; // 자세:2 호흡:4

    public static String NOTIFY_CODE_POSTURE_LEFT_DANGER        = "1100"; // 자세:3 호흡:5
    public static String NOTIFY_CODE_POSTURE_LEFT_EMERGENCY     = "1202"; // 자세:3 호흡:5

    public static String NOTIFY_CODE_POSTURE_RIGHT_DANGER       = "1101"; // 자세:4 호흡:5
    public static String NOTIFY_CODE_POSTURE_RIGHT_EMERGENCY    = "1203"; // 자세:4 호흡:5

    public static String NOTIFY_CODE_POSTURE_ROLLOVER_EMERGENCY = "1200"; // 자세:5 호흡:5

    public static String NOTIFY_CODE_POSTURE_UNKNOWN_DANGER     = "1102"; // 자세:6 호흡:5
    public static String NOTIFY_CODE_POSTURE_UNKNOWN_EMERGENCY  = "1201"; // 자세:6 호흡:5

    // Baby State
    public static final int STATE_GOOD      = 1; // 좋음
    public static final int STATE_DANGER    = 2; // 위험
    public static final int STATE_EMERGENCY = 3; // 응급

    // postureStatus
    public static final int POSTURE_STATUS_NOT_MONITORING = 0; // 비 모니터링
    public static final int POSTURE_STATUS_INITIALIZING   = 1; // 초기화중
    public static final int POSTURE_STATUS_GOOD           = 2; // 좋음
    public static final int POSTURE_STATUS_LEFT           = 3; // 왼쪽
    public static final int POSTURE_STATUS_RIGHT          = 4; // 오른쪽
    public static final int POSTURE_STATUS_ROLLOVER       = 5; // 뒤집힘
    public static final int POSTURE_STATUS_UNKNOWN        = 6; // 측정불가

    // breathStatus
    public static final int BREATH_STATUS_NOT_READY      = -1; // 계산 중
    public static final int BREATH_STATUS_NOT_MONITORING = 0; // 비 모니터링
    public static final int BREATH_STATUS_INITIALIZING   = 1; // 초기화중
    public static final int BREATH_STATUS_GOOD           = 2; // 좋음
    public static final int BREATH_STATUS_LOW            = 3; // 위험(저호흡)
    public static final int BREATH_STATUS_EMERGENCY      = 4; // 응급
    public static final int BREATH_STATUS_UNKNOWN        = 5; // 측정불가

    // 5분 히스토리 사용자 체크 없을 시 보정치
    public static final int COUNT_FIV = 5;
    public static final int COUNT_THR = 3;
    public static final int    DANGER_CORRECTION    = 2;
    public static final double EMERGENCY_CORRECTION = 2.5;

}