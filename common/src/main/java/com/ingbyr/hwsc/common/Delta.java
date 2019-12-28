package com.ingbyr.hwsc.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ingbyr
 */
public class Delta {

    private Set<String> inputConcepts;

    public Map<String, Double> distance = new HashMap<>();

    public Delta(Set<Concept> inputConcepts) {
        this.inputConcepts = inputConcepts.stream().map(Concept::getName).collect(Collectors.toSet());
        // init map
        inputConcepts.forEach(concept -> {
            distance.put(concept.getName(), 0.0);
        });
    }

    public void update(Service service) {
        Set<Concept> inputConcepts = service.getInputConceptSet();
        Set<Concept> outputConcepts = service.getOutputConceptSet();

        double inputMaxDelta = maxDeltaFromInputConcepts(inputConcepts);
        double serviceCost = service.getCost();

        outputConcepts.forEach(outputConcept -> {
            Double deltaWrapper = distance.get(outputConcept.getName());
            double delta = (deltaWrapper == null ? Double.MAX_VALUE : deltaWrapper);
            double newDelta = Math.min(delta, serviceCost + inputMaxDelta);

            // auto box to Double
            distance.put(outputConcept.getName(), newDelta);
        });
    }

    private double maxDeltaFromInputConcepts(Set<Concept> inputConcepts) {
        double maxDelta = 0.0;
        for (Concept inputConcept : inputConcepts) {
            maxDelta = Math.max(distance.get(inputConcept.getName()), maxDelta);
        }
        return maxDelta;
    }
}