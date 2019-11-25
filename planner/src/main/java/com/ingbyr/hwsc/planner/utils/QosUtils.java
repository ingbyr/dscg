package com.ingbyr.hwsc.planner.utils;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;

import java.util.Arrays;

/**
 * @author ingbyr
 */
public class QosUtils {
    public static Qos normalize(Qos qos) {
        Qos normalizedQos = new Qos();
        normalizedQos.set(Qos.RES, qos.get(Qos.RES));
        normalizedQos.set(Qos.AVA, trans2log(qos.get(Qos.AVA)));
        normalizedQos.set(Qos.SUC, trans2log(qos.get(Qos.SUC)));
        normalizedQos.set(Qos.REL, trans2log(qos.get(Qos.REL)));
        normalizedQos.set(Qos.LAT, qos.get(Qos.LAT));
        normalizedQos.set(Qos.PRI, qos.get(Qos.PRI));
        return normalizedQos;
    }

    private static double trans2log(double v) {
        return Math.log(1 - v);
    }

    public static double toSimpleSingeQos(Service service) {
        return Arrays.stream(service.getQos().getValues()).sum();
    }
}
