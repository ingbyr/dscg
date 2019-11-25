package com.ingbyr.hwsc.dataset.reader;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;

import java.util.Arrays;

import static com.ingbyr.hwsc.common.models.Qos.*;

/**
 * @author ingbyr
 */
public class QosUtils {
    public static Qos normalize(Qos qos) {
        Qos normalizedQos = new Qos();
        normalizedQos.set(RES, qos.get(RES));
        normalizedQos.set(AVA, trans2log(qos.get(AVA)));
        normalizedQos.set(SUC, trans2log(qos.get(SUC)));
        normalizedQos.set(REL, trans2log(qos.get(REL)));
        normalizedQos.set(LAT, qos.get(LAT));
        normalizedQos.set(PRI, qos.get(PRI));
        return normalizedQos;
    }

    private static double trans2log(double v) {
        return Math.log(1 - v);
    }

    public static double toSimpleSingeQos(Service service) {
        return Arrays.stream(service.getQos().getValues()).sum();
    }
}
