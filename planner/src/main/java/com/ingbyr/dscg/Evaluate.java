package com.ingbyr.dscg;

import com.ingbyr.dscg.planner.Planner;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Evaluate {
    /**
     * Evaluate the individual by someone planner
     * @param individuals Individuals
     * @param planner Planner
     */
    void evaluate(List<Individual> individuals, Planner planner);

    void setPlannerMaxStep(int plannerMaxStep);

    void setMaxStateSize(int maxStateSize);
}
