package babian.monitoring.demonstration.app.util;

import babian.monitoring.demonstration.app.constants.CommonConstants;
import babian.monitoring.demonstration.app.constants.RawDataProcessorConstants;
import babian.monitoring.demonstration.app.dto.MonitoringStatusDTO;
import org.jtransforms.fft.DoubleFFT_1D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

@Component
public class RawDataProcessor {

    private final Logger logger = LoggerFactory.getLogger("IOT_LOG_PROFILE");
    private final HashMap<Integer, Integer> patternMap = new HashMap<>();

    RawDataProcessor() {
        int[] patternAlready = new int[]{5333, 5363, 5633, 5663, 5444, 5464, 5644, 5664, 5336, 5366, 5636, 5666, 5446, 5466, 5646};
        int[] patternShort = new int[]{2233, 4233, 2244, 3244, 2333, 2444, 2223, 3223, 4223, 6223, 2224, 3224, 4224, 6224};
        int[] patternLong = new int[]{2336, 2446, 2366, 2466, 3666, 4666, 3366, 4466, 3336, 4446, 2236, 3236, 4236, 6236, 2246, 3246, 4246, 6246};
        for (int p : patternAlready) {
            patternMap.put(p, 1);
        }
        for (int p : patternShort) {
            patternMap.put(p, 2);
        }
        for (int p : patternLong) {
            patternMap.put(p, 3);
        }
    }

    private int getPosture(int[] dataArray) {
        double sum_ = dataArray[0] + dataArray[1] + dataArray[2];
        double leftRatio = sum_ == 0 ? 0 : dataArray[0] / sum_;
        double rightRatio = sum_ == 0 ? 0 : dataArray[2] / sum_;
        double midRatio = sum_ == 0 ? 0 : dataArray[1] / sum_;

        if (dataArray[0] < RawDataProcessorConstants.POSTURE_UNKNOWN_THRESHOLD
                && dataArray[1] < RawDataProcessorConstants.POSTURE_UNKNOWN_THRESHOLD
                && dataArray[2] < RawDataProcessorConstants.POSTURE_UNKNOWN_THRESHOLD) { // 3셀 모두 1이하
            return CommonConstants.POSTURE_STATUS_UNKNOWN; // 측정 불가
        }
        // 측정 가능, 기본적으로 마지막 압력값으로 자세 결정
        if (leftRatio > 0.33 && rightRatio > 0.33) {
            return CommonConstants.POSTURE_STATUS_UNKNOWN; // 알수없음
        }
        if (midRatio > 0.4) { // 중앙 값이 40% 초과
            return CommonConstants.POSTURE_STATUS_GOOD; // 정상

        } else if (dataArray[0] >= dataArray[1] && dataArray[0] >= dataArray[2]) { // 좌측 값이 가장 큼
            return CommonConstants.POSTURE_STATUS_LEFT; // 좌측

        } else if (dataArray[2] >= dataArray[1] && dataArray[2] >= dataArray[1]) { // 우측 값이 가장 큼
            return CommonConstants.POSTURE_STATUS_RIGHT; // 우측

        } else {
            return CommonConstants.POSTURE_STATUS_UNKNOWN; // 알수없음
        }
    }

    private boolean isRollOver(ArrayList<Integer> postureSequence, int[] dataArray) {
        double sum_ = dataArray[0] + dataArray[1] + dataArray[2];
        double midRatio = sum_ == 0 ? 0 : dataArray[1] / sum_;
        int patternKey = postureSequence.get(0) * 1000 + postureSequence.get(1) * 100
                + postureSequence.get(2) * 10 + postureSequence.get(3);
        if (patternMap.get(patternKey) == null) {
            return false;
        } else if (patternMap.get(patternKey) == 2) {
            return (midRatio < 0.1);
        }
        return true;
    }

    private double lpf(double x, double alpha, double prev) {
        return alpha * x + (1 - alpha) * prev;
    }

    public MonitoringStatusDTO getPressData(int timeStep, String dataString, MonitoringStatusDTO monitoringStatusDTO) {
        int currentPostureStatus;
        int[] dataArray0 = new int[11]; // 센서 셀 나눈 것
        int[] dataArray1 = new int[11]; // 센서 셀 나눈 것
        int[] dataArray2 = new int[11]; // 센서 셀 나눈 것

        if (dataString.length() != 120) {
            logger.info("데이터 누락! Data Length:" + dataString.length());
            return monitoringStatusDTO;
        }

        for (int i = 0; i < 3; i++) {
            dataArray0[i] = Integer.parseInt(dataString.substring(i * 2, (i * 2) + 2), 16);
            dataArray1[i] = Integer.parseInt(dataString.substring(i * 2 + 40, (i * 2) + 42), 16);
            dataArray2[i] = Integer.parseInt(dataString.substring(i * 2 + 80, (i * 2) + 82), 16);
        }
        for (int i = 0; i < 8; i++) {
            dataArray0[i + 3] = Integer.parseInt(dataString.substring(i * 4 + 6, (i * 4) + 10), 16);
            dataArray1[i + 3] = Integer.parseInt(dataString.substring(i * 4 + 46, (i * 4) + 50), 16);
            dataArray2[i + 3] = Integer.parseInt(dataString.substring(i * 4 + 86, (i * 4) + 90), 16);
        }

        // 일반 자세 검사
        currentPostureStatus = getPosture(dataArray2);

        // 4초간의 자세 시퀀스
        ArrayList<Integer> postureSequence = new ArrayList<>();
        postureSequence.add(monitoringStatusDTO.postureStatus);
        postureSequence.add(getPosture(dataArray0));
        postureSequence.add(getPosture(dataArray1));
        postureSequence.add(currentPostureStatus);

        // 뒤집힘 체크
        if (isRollOver(postureSequence, dataArray2)) {
            currentPostureStatus = CommonConstants.POSTURE_STATUS_ROLLOVER;
        }

        monitoringStatusDTO.postureStatus = currentPostureStatus;

        if (currentPostureStatus == CommonConstants.POSTURE_STATUS_GOOD) {
            int[] sampleDataArray = new int[24];

            for (int i = 0; i < 8; i++) {
                sampleDataArray[i] = dataArray0[i + 3];
                sampleDataArray[i + 8] = dataArray1[i + 3];
                sampleDataArray[i + 16] = dataArray2[i + 3];
            }
            monitoringStatusDTO = setPositionValue(monitoringStatusDTO, timeStep, 2, sampleDataArray);
        } else {
            setPositionValue(monitoringStatusDTO);
        }
        return monitoringStatusDTO;
    }

    public void setPositionValue(MonitoringStatusDTO matNode) {
        matNode.postureValue = 0;
        matNode.postureSumValue = 0;
        matNode.prevTimeStep = 0;
        matNode.breathRateValue = 0;
        matNode.breathStatus = CommonConstants.BREATH_STATUS_UNKNOWN;
    }

    // 자세/호흡 상태 구하기
    public MonitoringStatusDTO setPositionValue(MonitoringStatusDTO monitoringStatusDTO, int timeStep, int position, int[] data) {
        int minLen = RawDataProcessorConstants.LOWER_BOUND_LENGTH;
        int maxLen = RawDataProcessorConstants.UPPER_BOUND_LENGTH;
        double f_cut = RawDataProcessorConstants.CUT_OFF_FREQUENCY;
        int window_size = RawDataProcessorConstants.WINDOW_SIZE;

        if (monitoringStatusDTO.prevTimeStep != timeStep) {
            monitoringStatusDTO.prevTimeStep = timeStep;
            if (monitoringStatusDTO.postureValue == position) {
                for (int datum : data) { // 'data.length' should be 8*3
                    monitoringStatusDTO.pressureData.add((double) datum);
                }

                monitoringStatusDTO.postureSumValue += 1;

                monitoringStatusDTO.breathStatus = CommonConstants.BREATH_STATUS_NOT_READY;
                if (monitoringStatusDTO.postureSumValue >= minLen) {
                    // 1. Get Moving Average Filtered Pressure Data
                    LinkedList<Double> ma_filtered = new LinkedList<>();

                    int i = 0;
                    for (i = 0; i + window_size < monitoringStatusDTO.pressureData.size(); i += window_size) {
                        double window_sum = 0;
                        for (int j = 0; j < window_size; ++j) {
                            window_sum += monitoringStatusDTO.pressureData.get(i + j);
                        }
                        for (int j = 0; j < window_size; ++j) {
                            ma_filtered.add(window_sum / window_size);
                        }
                    }

                    for (int j = i - window_size; j < monitoringStatusDTO.pressureData.size(); ++j) {
                        ma_filtered.add(monitoringStatusDTO.pressureData.get(j));
                    }

                    // 2. Subtract MA Filtered Pressure Data from Original Raw Data
                    for (i = 0; i < monitoringStatusDTO.pressureData.size(); ++i) {
                        ma_filtered.set(i, (monitoringStatusDTO.pressureData.get(i) - ma_filtered.get(i)));
                    }

                    // 3. Low Pass Filter
                    LinkedList<Double> lpf_filtered = new LinkedList<>();
                    double prev = ma_filtered.get(0);
                    double alpha = (2 * Math.PI * f_cut) / (8 + 2 * Math.PI * f_cut);
                    for (Double aDouble : ma_filtered) {
                        double curr = lpf(aDouble, alpha, prev);
                        lpf_filtered.add(curr);
                        prev = curr;
                    }

                    // 4. FFT
                    DoubleFFT_1D fft = new DoubleFFT_1D(lpf_filtered.size());
                    Object[] objArray = lpf_filtered.toArray();
                    Double[] dObjArray = Arrays.copyOf(objArray, objArray.length, Double[].class);
                    double[] pressureData = Arrays.stream(dObjArray).mapToDouble(Double::doubleValue).toArray();
                    fft.realForward(pressureData);

                    double sample_freq = 8.0;
                    double[] re = new double[((pressureData.length - 1) / 2) + 1];
                    double[] im = new double[((pressureData.length - 1) / 2) + 1];

                    im[(pressureData.length - 1) / 2] = pressureData[1];
                    for (i = 2; i < pressureData.length - 2; i += 2) {
                        re[i / 2] = pressureData[i];
                        im[i / 2] = pressureData[(i + 1)];
                    }
                    re[(pressureData.length - 1) / 2] = pressureData[pressureData.length - 1];

                    int peakIndex = 0;
                    double maxMag = 0;
                    for (i = 1; i < re.length; ++i) {
                        double magnitude = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
                        if (magnitude > maxMag && (i * sample_freq / pressureData.length) < 100) {
                            maxMag = magnitude;
                            peakIndex = i;
                        }
                    }

                    double estimated_freq = peakIndex * sample_freq / pressureData.length;
                    double freqPerMin = estimated_freq * 60;
                    logger.info("호흡 수: " + freqPerMin);

                    int breathStatus;

                    if (freqPerMin < RawDataProcessorConstants.BREATH_RR_EMERGENCY) {
                        monitoringStatusDTO.breathRateValue = 3;
                        breathStatus = CommonConstants.BREATH_STATUS_EMERGENCY;
                    } else if (freqPerMin < RawDataProcessorConstants.BREATH_RR_LOW) {
                        monitoringStatusDTO.breathRateValue = 1;
                        breathStatus = CommonConstants.BREATH_STATUS_LOW;
                    } else if (freqPerMin < RawDataProcessorConstants.BREATH_RR_GOOD) {
                        monitoringStatusDTO.breathRateValue = 2;
                        breathStatus = CommonConstants.BREATH_STATUS_GOOD;
                    } else { // 50 이상
                        monitoringStatusDTO.breathRateValue = 3;
                        breathStatus = CommonConstants.BREATH_STATUS_EMERGENCY;
                    }

                    if (monitoringStatusDTO.postureSumValue >= maxLen) {
                        // Remove the oldest pressure data
                        for (i = 0; i < 8 * 3; ++i) {
                            monitoringStatusDTO.pressureData.poll();
                        }
                        monitoringStatusDTO.postureSumValue--;
                    }
                    monitoringStatusDTO.breathStatus = breathStatus;
                }
            } else {
                monitoringStatusDTO.postureValue = position;
                monitoringStatusDTO.postureSumValue = 0;
                monitoringStatusDTO.pressureData.clear();
                monitoringStatusDTO.breathStatus = CommonConstants.BREATH_STATUS_NOT_READY;
            }
            monitoringStatusDTO.postureValue = position;
        } else {
            monitoringStatusDTO.breathStatus = CommonConstants.BREATH_STATUS_UNKNOWN;
        }

        return monitoringStatusDTO;
    }
}