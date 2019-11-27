package com.ingbyr.hwsc.planner.innerplanner;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author ingbyr
 */
public abstract class AbstractInnerPlanner implements InnerPlanner {

    protected Map<String, Service> serviceMap;

    protected Map<String, Concept> conceptMap;

    protected Set<Concept> inputSet;

    protected Set<Concept> goalSet;

    public AbstractInnerPlanner(Map<String, Service> serviceMap, Map<String, Concept> conceptMap) {
        this.serviceMap = serviceMap;
        this.conceptMap = conceptMap;
    }
}
