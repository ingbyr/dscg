package com.ingbyr.hwsc.planner;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Evaluator {
    /**
     * Evaluate the individual by someone planner
     * @param individuals Individuals
     * @param planner Planner
     */
    void evaluate(List<Individual> individuals, Planner planner);
}
