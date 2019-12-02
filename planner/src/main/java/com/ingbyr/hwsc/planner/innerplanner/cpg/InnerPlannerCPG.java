package com.ingbyr.hwsc.planner.innerplanner.cpg;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.Solution;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.*;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.PlanningGraph;
import com.ingbyr.hwsc.planner.pg.searching.ForwardPlanningGraphSearcher;
import com.ingbyr.hwsc.planner.pg.searching.PlanningGraphSearcher;
import com.ingbyr.hwsc.planner.innerplanner.AbstractInnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.cpg.extractors.PlanExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ingbyr
 */
@Slf4j
public class InnerPlannerCPG extends AbstractInnerPlanner implements InnerPlanner {

    private PlanExtractor extractor;

    public InnerPlannerCPG(Map<String, Service> serviceMap, Map<String, Concept> conceptMap, PlanExtractor extractor) {
        super(serviceMap, conceptMap);
        this.extractor = extractor;
    }

    @Override
    public Solution solve(Set<Concept> inputSet, Set<Concept> goalSet, int boundary) {

        log.debug("Input set {}", inputSet);
        log.debug("Goal set {}", goalSet);
        if (inputSet.containsAll(goalSet)) {
            return new Solution(new LinkedList<>(), 0);
        }

        PlanningGraph pg = new PlanningGraph(conceptMap);
        pg.addPLevel(Sets.newHashSet(inputSet));
        pg.setInputSet(Sets.newHashSet(inputSet));
        pg.setGoalSet(Sets.newHashSet(goalSet));
        pg.setServiceMap(serviceMap);
        pg.addALevel(new HashSet<>());

        PlanningGraphSearcher searcher = new ForwardPlanningGraphSearcher(pg, conceptMap, serviceMap);
        boolean forwardSearchResult = searcher.search();
        log.debug("Forward search result: {}", forwardSearchResult);

        CompletePlaningGraph cpg = new CompletePlaningGraph();
        cpg.build(pg);
        extractor.setCpg(cpg);
        extractor.find();
        return createSolution(extractor.getPaths(), extractor.getSteps());
    }

    @Override
    public InnerPlanner copy() {
        return new InnerPlannerCPG(serviceMap, conceptMap, extractor);
    }

    private Solution createSolution(List<GraphPath<DWGNode, DWGEdge>> paths, int bDone) {
        List<String> services = new LinkedList<>();
        double cost = 0.0;
        for (GraphPath<DWGNode, DWGEdge> path : paths) {
            for (DWGEdge dwgEdge : path.getEdgeList()) {
                for (LeveledService leveledService : dwgEdge.getServices()) {
                    services.add(leveledService.getService());
                    cost += leveledService.getCost();
                }
            }
        }
        List<Service> s = services.stream().map(DatasetCache::getService).collect(Collectors.toList());
        Solution solution = new Solution(s, cost);
        log.debug("Find {}", solution);
        return solution;
    }
}
