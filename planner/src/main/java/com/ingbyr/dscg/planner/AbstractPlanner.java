package com.ingbyr.dscg.planner;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Service;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * @author ingbyr
 */
@NoArgsConstructor
@Setter
public abstract class AbstractPlanner implements Planner {

    protected Map<String, Service> serviceMap;

    protected Map<String, Concept> conceptMap;

    protected Set<Concept> inputSet;

    protected Set<Concept> goalSet;
}
