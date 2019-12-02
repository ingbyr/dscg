package com.ingbyr.hwsc.planner.innerplanner.cpg.models;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Delta;
import com.ingbyr.hwsc.common.models.Service;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * The class represents a planning graph
 */
@ToString
public class PlanningGraph {

    private Vector<Set<Concept>> PLevels;
    private Vector<Set<Service>> ALevels;

    @Getter
    @Setter
    // Immutable list
    private Set<Concept> goalSet;

    @Getter
    @Setter
    // Immutable list
    private Set<Concept> inputSet;

    @Getter
    @Setter
    private Map<String, Service> serviceMap;

    @Getter
    @Setter
    private Map<String, Concept> conceptMap;

    public LinkedList<LinkedHashSet<Service>> actionLevels = new LinkedList<>();
    public LinkedHashSet<LinkedHashSet<Concept>> propLevels = new LinkedHashSet<>();

    private Service startService;
    private Service targetService;
    public List<Service> target;
    public List<Service> start;

    // for heuristic searching
    public Delta delta;

    public PlanningGraph(Map<String, Concept> conceptMap) {
        PLevels = new Vector<>();
        ALevels = new Vector<>();
        targetService = new Service("target");
        target = Collections.singletonList(targetService);
        startService = new Service("start");
        start = Collections.singletonList(startService);
        this.conceptMap = conceptMap;
    }

    public Vector<Set<Concept>> getPLevels() {
        return PLevels;
    }

    public Vector<Set<Service>> getALevels() {
        return ALevels;
    }

    public Set<Concept> getPLevel(int index) {
        return this.PLevels.get(index);
    }

    public Set<Service> getALevel(int index) {
        return this.ALevels.get(index);
    }

    public void setALevel(int index, Set<Service> ALevel) {
        this.ALevels.set(index, ALevel);
    }

    public void addPLevel(Set<Concept> level) {
        this.PLevels.add(level);
    }

    public void addALevel(Set<Service> level) {
        this.ALevels.add(level);
    }

    public void initCache() {
        // Init dataset cache
        DatasetCache.serviceMap = new HashMap<>(serviceMap);
        DatasetCache.conceptMap = new HashMap<>(conceptMap);
        DatasetCache.serviceMap.put("start", startService);
        DatasetCache.serviceMap.put("target", targetService);
    }
}
