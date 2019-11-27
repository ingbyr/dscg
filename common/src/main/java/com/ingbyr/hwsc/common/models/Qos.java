package com.ingbyr.hwsc.common.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @author ingbyr
 */
@Getter
@ToString
@EqualsAndHashCode
public class Qos {

    // QoS index
    public static final int RES = 0;
    public static final int AVA = 1;
    public static final int SUC = 2;
    public static final int REL = 3;
    public static final int LAT = 4;
    public static final int PRI = 5;

    // Qos type
    public static final int[] TYPES = new int[]{
            RES, AVA, SUC, REL, REL, LAT, PRI
    };

    // Qos that need flip
    public static final int[] FLIP_QOS_TYPES = new int[]{
            AVA, SUC, REL
    };

    // Qos name
    public static final String[] NAMES = new String[]{
            "Res", "Ava", "Suc", "Rel", "Lat", "Pri"
    };

    // Qos total number
    public static final int QOS_NUM = TYPES.length;

    // Qos values
    private final double[] values = new double[QOS_NUM];

    public Qos() {
        Arrays.fill(values, 0.0);
    }

    public Qos(Double initialValue) {
        Arrays.fill(values, initialValue);
    }

    public void set(int type, double value) {
        values[type] = value;
    }

    public double get(int type) {
        return values[type];
    }
}
