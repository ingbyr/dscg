package com.ingbyr.hwsc.planner;

import java.util.Arrays;
import java.util.List;

/**
 * @author ingbyr
 */
public interface Fitness {

    void calculatePopulationFitness(List<Individual> population);

    static List<String> getAllNames() {
        return Arrays.asList("LinearAggregation", "BinaryIndicator", "ParetoFront");
    }
}
