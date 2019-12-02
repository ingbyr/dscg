package com.ingbyr.hwsc.planner;

import java.util.Arrays;
import java.util.List;

/**
 * @author ingbyr
 */
public class IndicatorSum implements Indicator {

    @Override
    public void calculatePopulationFitness(List<Individual> population) {
        population.forEach(individual -> {
            individual.setFitness(Arrays.stream(individual.getQos().getValues()).sum());
        });
    }
}
