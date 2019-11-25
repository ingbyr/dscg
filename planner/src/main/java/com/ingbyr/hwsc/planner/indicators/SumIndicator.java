package com.ingbyr.hwsc.planner.indicators;

import com.ingbyr.hwsc.planner.Individual;

import java.util.Arrays;
import java.util.List;

/**
 * @author ingbyr
 */
public class SumIndicator implements Indicator {

    @Override
    public void calcFitness(List<Individual> population) {
        population.forEach(individual -> {
            individual.setFitness(Arrays.stream(individual.getQos().getValues()).sum());
        });
    }
}
