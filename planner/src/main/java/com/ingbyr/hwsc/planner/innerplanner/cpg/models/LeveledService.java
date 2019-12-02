package com.ingbyr.hwsc.planner.innerplanner.cpg.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Service wrapper. Add level properties to Service
 *
 * @author ingbyr
 */
@EqualsAndHashCode
public class LeveledService {

    @Getter
    private String service;

    @Getter
    private int level;

    @Setter
    private Set<String> inputConceptSet;

    @Setter
    private Set<String> outputConceptSet;

    public LeveledService(String service, int level) {
        this.service = service;
        this.level = level;
    }

    @Override
    public String toString() {
        return service + "@L" + level + "@C" + DatasetCache.getCost(service);
    }

    public double getCost() {
        return DatasetCache.getCost(service);
    }

    public Set<String> getInputConceptSet() {
        return inputConceptSet == null ? DatasetCache.getInputSetOfService(service) : inputConceptSet;
    }

    public Set<String> getOutputConceptSet() {
        return outputConceptSet == null ? DatasetCache.getOutputSetOfService(service) : outputConceptSet;
    }
}