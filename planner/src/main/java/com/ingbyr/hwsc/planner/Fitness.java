package com.ingbyr.hwsc.planner;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Fitness {
    /**
     * Calculate individual fitness and sort population
     * @param pop Population
     * @return The biggest fitness
     */
    double calc(List<Individual> pop);
}
