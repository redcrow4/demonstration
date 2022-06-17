package com.midashnt.babian.ko.iot.constants;

public class IoTConstants {

    // IoT information
    public static String IoT_ENDPOINT  = "";
    public static String IoT_CLIENT_ID = "";

    // AWS Access Key
    public static String AWS_ACCESS_KEY_ID     = "";
    public static String AWS_SECRET_ACCESS_KEY = "";
    public static int    AWS_IoT_CLIENT_COUNT  = 1;

    // iot message type
    public static final String IoT_LIGHT_TYPE_POWER_STATUS   = "01"; // 수유등에 등록된 매트의 모니터링 On/Off 명령
    public static final String IoT_LIGHT_TYPE_PUT_MAC_ADDRESS= "02"; // 수유등에 매트 Mac address 입력
    public static final String IoT_LIGHT_TYPE_LED_CONTROL    = "03"; // 수유등의 LED 컨트롤(ON/OFF, 색상)
    public static final String IoT_LIGHT_TYPE_LED_SCHEDULE   = "04"; // 수유등의 LED 스케줄 등록(ON/OFF, 색상, 타이머)
    public static final String IoT_LIGHT_TYPE_ECHO           = "05"; // 애코(수유등 전원인가 확인-요청)

    // iot topic
    public static final String IoT_TOPIC_LIGHT = "light/";

    public static final String IoT_BASIC_INGEST_PREFIX = "$aws/rules/";
    public static final String IoT_RULE_Babian_ko_RawData_Prod = "Babian_ko_RawData_Prod";

}
