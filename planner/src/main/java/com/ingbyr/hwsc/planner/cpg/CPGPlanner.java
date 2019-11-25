package com.ingbyr.hwsc.planner.cpg;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dae.Planner;
import com.ingbyr.hwsc.dae.Solution;
import com.ingbyr.hwsc.model.PlanningGraph;
import com.ingbyr.hwsc.pg.searching.ForwardPlanningGraphSearcher;
import com.ingbyr.hwsc.pg.searching.PlanningGraphSearcher;
import com.ingbyr.hwsc.planner.AbstractPlanner;
import com.ingbyr.hwsc.planner.cpg.extractors.PlanExtractor;
import com.ingbyr.hwsc.planner.cpg.models.CompletePlaningGraph;
import com.ingbyr.hwsc.planner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.cpg.models.DWGNode;
import com.ingbyr.hwsc.planner.cpg.models.LeveledService;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;

import java.util.*;

/**
 * @author ingbyr
 */
@Slf4j
public class CPGPlanner extends AbstractPlanner implements Planner {

    private PlanExtractor extractor;

    public CPGPlanner(Map<String, Service> serviceMap, Map<String, Concept> conceptMap, PlanExtractor extractor) {
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
    public Planner copy() {
        return new CPGPlanner(serviceMap, conceptMap, extractor);
    }

    private Solution createSolution(List<GraphPath<DWGNode, DWGEdge>> paths, int bDone) {
        List<Service> services = new LinkedList<>();
        double cost = 0.0;
        for (GraphPath<DWGNode, DWGEdge> path : paths) {
            for (DWGEdge dwgEdge : path.getEdgeList()) {
                for (LeveledService leveledService : dwgEdge.getServices()) {
                    services.add(leveledService.getService());
                    cost += leveledService.getService().getCost();
                }
            }
        }
        Solution solution = new Solution(services, cost);
        log.debug("Find {}", solution);
        return solution;
    }
}
