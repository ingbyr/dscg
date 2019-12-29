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

    public static Qos mergeOriginQos(Collection<Service> services) {
        Qos qos = new Qos();
        // Update qos
        for (Service service : services) {
            for (int type : Qos.TYPES) {
                    qos.set(type, qos.get(type) + service.getOriginQos().get(type));
            }
        }
        return qos;
    }

//    public static Qos mergeOriginQos1(Collection<Service> services) {
//        Qos qos = new Qos();
//        // Update qos
//        for (Service service : services) {
//            for (int type : Qos.TYPES) {
//                qos.set(type, qos.get(type) + service.getOriginQos().get(type));
//            }
//        }
//        return qos;
//    }

//    public static Qos mergeOriginQos2(Collection<Service> services) {
//        Qos qos = new Qos(1.0);
//        // Update qos
//        for (Service service : services) {
//            for (int type : Qos.TYPES) {
//                qos.set(type, qos.get(type) * (1 - service.getOriginQos().get(type)));
//            }
//        }
//        return qos;
//    }
}
