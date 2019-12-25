package com.ingbyr.hwsc.common.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * @author ingbyr
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Qos {

    // QoS index
    public static final int RES = 0;
    public static final int LAT = 1;
    public static final int PRI = 2;

    public static final int AVA = 3;
    public static final int SUC = 4;
    public static final int REL = 5;

    public static int FLIP_INDEX = 3;

    // Qos type
    public static final int[] TYPES = new int[]{
            RES, LAT, PRI, AVA, SUC, REL
    };

    public static final int[] ACTIVE_TYPES = new int[]{
            RES, LAT, PRI, AVA, SUC, REL
    };

    // Qos name
    public static final String[] NAMES = new String[]{
            "Res", "Lat", "Pri", "Ava", "Suc", "Rel"
    };

    // Qos total number
    public static final int QOS_NUM = TYPES.length;

    // Qos values
    @Setter
    private double[] values = new double[QOS_NUM];


    public Qos(Double initialValue) {
        Arrays.fill(values, initialValue);
    }

    public void set(int type, double value) {
        values[type] = value;
    }

    public double get(int type) {
        return values[type];
    }


    @Override
    public String toString() {
        StringBuilder qosStr = new StringBuilder();
        qosStr.append('[');
        for (int type : ACTIVE_TYPES) {
            qosStr.append(String.format("%.1f", values[type]));
            qosStr.append(',');
        }
        qosStr.deleteCharAt(qosStr.length() - 1);
        qosStr.append(']');
        return qosStr.toString();
    }
}
