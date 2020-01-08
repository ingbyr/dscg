package com.ingbyr.hwsc.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author ingbyr
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Slf4j
public class Qos implements NumpyDataFormat {

    private static final String PRECISION = "%.1f";

    // QoS index
    public static final int RES = 0;
    public static final int LAT = 1;
    public static final int PRI = 2;

    public static final int AVA = 3;
    public static final int SUC = 4;
    public static final int REL = 5;

    // Qos total types
    private static final int[] RAW_TYPES = new int[]{
            RES, LAT, PRI, AVA, SUC, REL
    };

    // Qos total names
    private static final String[] RAW_NAMES = new String[]{
            "Res", "Lat", "Pri", "Ava", "Suc", "Rel"
    };

    public static int[] TYPES;

    public static String TYPES_STRING;

    public static String[] NAMES;

    static {
        String qosTypes = System.getenv("HWSC_QOS_TYPES");
        try {
            TYPES = Arrays.stream(qosTypes.split(" ")).mapToInt(Integer::valueOf).sorted().toArray();
            TYPES_STRING = qosTypes.replaceAll(" ", "");
        } catch (Exception e) {
            TYPES = new int[RAW_TYPES.length];
            System.arraycopy(RAW_TYPES, 0, TYPES, 0, RAW_TYPES.length);
            log.info("You can set qos types by \"HWSC_QOS_TYPES\"");
        }
        int QosActiveSize = TYPES.length;
        NAMES = new String[QosActiveSize];
        for (int i = 0; i < QosActiveSize; i++) {
            NAMES[i] = RAW_NAMES[TYPES[i]];
            TYPES[i] = i;
        }
    }

    // Qos total number
    public static final int RAW_QOS_NUM = RAW_TYPES.length;

    public static final int QOS_NUM = TYPES.length;

    // Qos values
    @Setter
    private double[] data = new double[QOS_NUM];

    public Qos(Double initialValue) {
        Arrays.fill(data, initialValue);
    }

    public Qos(double[] data) {
        this.data = data;
    }

    public static Qos ofNumpyFormat(String valueStr) {
        return new Qos(Arrays.stream(valueStr.split(" ")).mapToDouble(Double::valueOf).toArray());
    }

    public void set(int type, double value) {
        data[type] = value;
    }

    public double get(int type) {
        return data[type];
    }

    @Override
    public String toString() {
        StringBuilder qStr = new StringBuilder();
        qStr.append('[');
        for (double v : data) {
            qStr.append(String.format(PRECISION, v));
            qStr.append(',');
        }
        qStr.deleteCharAt(qStr.length() - 1);
        qStr.append(']');
        return qStr.toString();
    }

    @Override
    public String toNumpy() {
        StringBuilder vStr = new StringBuilder();
        for (double v : data) {
            vStr.append(String.format(PRECISION, v));
            vStr.append(' ');
        }
        return vStr.toString();
    }
}
