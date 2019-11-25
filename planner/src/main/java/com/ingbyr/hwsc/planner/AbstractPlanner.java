package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dae.Planner;

import java.util.Map;
import java.util.Set;

/**
 * @author ingbyr
 */
public abstract class AbstractPlanner implements Planner {

    protected Map<String, Service> serviceMap;

    protected Map<String, Concept> conceptMap;

    protected Set<Concept> inputSet;

    protected Set<Concept> goalSet;

    public AbstractPlanner(Map<String, Service> serviceMap, Map<String, Concept> conceptMap) {
        this.serviceMap = serviceMap;
        this.conceptMap = conceptMap;
    }
}
