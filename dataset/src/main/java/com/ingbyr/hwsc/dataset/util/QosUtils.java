package com.ingbyr.hwsc.dataset.util;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;
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
        Qos flippedQos = copy(qos);
        for (int type : FLIP_QOS_TYPES) {
            flippedQos.set(type, -qos.get(type));
        }
        return flippedQos;
    }

    public static double toSimpleCost(Service service) {
        return Arrays.stream(service.getQos().getValues()).sum();
    }

    public static Qos copy(Qos qos) {
        Qos newQos = new Qos();
        for (int type : TYPES) {
            newQos.set(type, qos.get(type));
        }
        return newQos;
    }
}
