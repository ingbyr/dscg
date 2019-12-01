package com.ingbyr.hwsc.dataset.util;

import com.ingbyr.hwsc.common.models.Qos;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.ingbyr.hwsc.common.models.Qos.FLIP_QOS_TYPES;
import static com.ingbyr.hwsc.common.models.Qos.TYPES;

/**
 * @author ingbyr
 */
@Slf4j
public class QosUtils {

    public static Qos flip(Qos qos) {
        Qos newQos = QosUtils.copy(qos);
        for (int type : FLIP_QOS_TYPES) {
            newQos.set(type, 1 - qos.get(type));
        }
        return newQos;
    }

    public static Qos flip(int offset, Qos qos) {
        Qos newQos = QosUtils.copy(qos);
        for (int type : FLIP_QOS_TYPES) {
            newQos.set(type, -qos.get(type));
        }
        return newQos;
    }

    public static double toSimpleCost(Qos qos) {
        return Arrays.stream(qos.getValues()).sum();
    }

    public static Qos copy(Qos qos) {
        Qos newQos = new Qos();
        for (int type : TYPES) {
            newQos.set(type, qos.get(type));
        }
        return newQos;
    }
}
