package com.ingbyr.hwsc.dae;

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
