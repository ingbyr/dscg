package com.hwsc.baseline.cpg.models;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * Service wrapper. Add level properties to Service
 *
 * @author ingbyr
 */
@EqualsAndHashCode
public class LeveledService {
    @Getter
    Service service;

    public int level;

    @EqualsAndHashCode.Exclude
    private Set<Concept> inputConceptSet;

    @EqualsAndHashCode.Exclude
    private Set<Concept> outputConceptSet;

    public LeveledService(Service service, int level) {
        this.service = service;
        this.level = level;
    }

    @Override
    public String toString() {
        return service + "@" + level;
    }

    public double getCost() {
        return service.getCost();
    }

    public Set<Concept> getInputConceptSet() {
        if (inputConceptSet == null)
            return service.getInputConceptSet();
        else
            return inputConceptSet;
    }

    public void setInputConceptSet(Set<Concept> inputConceptSet) {
        this.inputConceptSet = inputConceptSet;
    }

    public Set<Concept> getOutputConceptSet() {
        if (outputConceptSet == null) {
            return service.getOutputConceptSet();
        } else
            return outputConceptSet;
    }

    public void setOutputConceptSet(Set<Concept> outputConceptSet) {
        this.outputConceptSet = outputConceptSet;
    }
}