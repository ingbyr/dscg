package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Concept;

import java.io.Serializable;
import java.util.Set;

/**
 * @author ingbyr
 */
public interface Planner extends Serializable {

    /**
     * Solve the plan problem
     *
     * @param inputSet Input concept set
     * @param goalSet  Goal concept set
     * @param boundary Boundary of search steps
     * @return Solution of plan problem
     */
    Solution solve(Set<Concept> inputSet, Set<Concept> goalSet, int boundary);

    Planner copy();
}
