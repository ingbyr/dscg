package com.ingbyr.hwsc.common;

import java.util.Arrays;
import java.util.Collection;

public class QosUtils {

    public static double sumQosToCost(Qos qos) {
        return Arrays.stream(qos.getData()).sum();
    }

    public static Qos mergeQos(Collection<Service> services) {
        Qos qos = new Qos();
        // Update qos
        for (Service service : services) {
            for (int type : Qos.TYPES) {
                qos.set(type, qos.get(type) + service.getQos().get(type));
            }
        }
        return qos;
    }

    public static Qos mergeRawQos(Collection<Service> services) {
        Qos rawQos = new Qos();
        // Update raw qos
        for (Service service : services) {
            for (int type : Qos.TYPES) {
                    rawQos.set(type, rawQos.get(type) + service.getOriginQos().get(type));
            }
        }
        return rawQos;
    }
}
