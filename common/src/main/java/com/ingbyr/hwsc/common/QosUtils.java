package com.ingbyr.hwsc.common;

import java.util.Arrays;
import java.util.Collection;

public class QosUtils {

    public static double sumQosToCost(QoS qos) {
        return Arrays.stream(qos.getValues()).sum();
    }

    public static QoS mergeQos(Collection<Service> services) {
        QoS qos = new QoS();
        // Update qos
        for (Service service : services) {
            for (int type : QoS.TYPES) {
                qos.set(type, qos.get(type) + service.getQos().get(type));
            }
        }
        return qos;
    }

    public static QoS mergeOriginQos(Collection<Service> services) {
        QoS qos = new QoS();
        // Update qos
        for (Service service : services) {
            for (int type : QoS.TYPES) {
                    qos.set(type, qos.get(type) + service.getOriginQoS().get(type));
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
