package com.ingbyr.dscg;

import java.util.Arrays;
import java.util.List;

/**
 * @author ingbyr
 */
public class FitnessLinearAggregation implements Fitness {

    @Override
    public double calc(List<Individual> pop) {
        double max = Double.MIN_VALUE;
        for (Individual ind : pop) {
            double f = Arrays.stream(ind.getQos().getData()).sum();
            ind.setFitness(f);
            max = Math.max(max, f);
        }
        return max;
    }

}
