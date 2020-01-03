package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Evaluate {
    /**
     * Evaluate the individual by someone planner
     * @param individuals Individuals
     * @param innerPlanner Planner
     */
    void evaluate(List<Individual> individuals, InnerPlanner innerPlanner);

    void setInnerPlannerMaxStep(int innerPlannerMaxStep);

    void setMaxStateSize(int maxStateSize);
}
