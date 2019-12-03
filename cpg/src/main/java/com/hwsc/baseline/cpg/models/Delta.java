package com.hwsc.baseline.cpg.models;

import com.google.common.collect.Maps;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author ingbyr
 */
public class Delta {

    private Set<Concept> inputConcepts;

    public Map<Concept, Double> distance;

    public Delta(Set<Concept> inputConcepts) {
        this.inputConcepts = inputConcepts;
        distance = Maps.newHashMap();
        // init map
        inputConcepts.forEach(concept -> {
            distance.put(concept, 0.0);
        });
    }

    public void update(Service service) {
        Set<Concept> inputConcepts = service.getInputConceptSet();
        Set<Concept> outputConcepts = service.getOutputConceptSet();

        double inputMaxDelta = maxDeltaFromInputConcepts(inputConcepts);
        double serviceCost = service.getCost();

        outputConcepts.forEach(outputConcept -> {
            Double deltaWrapper = distance.get(outputConcept);
            double delta = (deltaWrapper == null ? Double.MAX_VALUE : deltaWrapper);
            double newDelta = Math.min(delta, serviceCost + inputMaxDelta);

            // auto box to Double
            distance.put(outputConcept, newDelta);
        });
    }

    private double maxDeltaFromInputConcepts(Set<Concept> inputConcepts) {
        double maxDelta = 0.0;
        for (Concept inputConcept : inputConcepts) {
            maxDelta = Math.max(distance.get(inputConcept), maxDelta);
        }
        return maxDelta;
    }
}