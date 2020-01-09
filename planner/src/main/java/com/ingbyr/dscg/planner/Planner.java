package com.ingbyr.dscg.planner;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author ingbyr
 */
public interface Planner extends Serializable {

    void setServiceMap(Map<String, Service> serviceMap);

    void setConceptMap(Map<String, Concept> conceptMap);

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
