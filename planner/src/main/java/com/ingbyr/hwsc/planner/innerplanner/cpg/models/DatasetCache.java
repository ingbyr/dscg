package com.ingbyr.hwsc.planner.innerplanner.cpg.models;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DatasetCache {

    public static Map<String, Service> serviceMap = new HashMap<>();

    public static Map<String, Concept> conceptMap = new HashMap<>();

    public static double getCost(String service) {
        return serviceMap.get(service).getCost();
    }

    public static Set<String> getInputSetOfService(String service) {
        return toStrSet(serviceMap.get(service).getInputConceptSet());
    }

    public static Set<String> getOutputSetOfService(String service) {
        return toStrSet(serviceMap.get(service).getOutputConceptSet());
    }

    public static Set<String> toStrSet(Set<Concept> conceptSet) {
        return conceptSet.stream().map(Concept::getName).collect(Collectors.toSet());
    }

    public static Service getService(String service) {
        return serviceMap.get(service);
    }
}
