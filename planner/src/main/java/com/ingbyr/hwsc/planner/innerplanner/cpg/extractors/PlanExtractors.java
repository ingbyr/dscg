package com.ingbyr.hwsc.planner.innerplanner.cpg.extractors;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.CompletePlaningGraph;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Set;

@Slf4j
public final class PlanExtractors {

    public static void assertValidPaths(List<GraphPath<DWGNode, DWGEdge>> paths) {
        log.debug("Validating paths ...");
        for (GraphPath<DWGNode, DWGEdge> path : paths) {

            Set<String> output = path.getStartVertex().getOutputConcepts();
//            log.debug("services: {}#{}", path.getStartVertex().getServices(), 0.0);

            for (DWGEdge edge : path.getEdgeList()) {
//                log.debug("services: {}#{}", edge.getServices(), edge.getWeight());
                if (!output.containsAll(CompletePlaningGraph.mergeInputConcepts(edge.getServices())))
                    throw new RuntimeException("Not valid edge: " + edge);
                output.addAll(CompletePlaningGraph.mergeOutputConcepts(edge.getServices()));
            }
        }
        log.debug("All {} path are valid", paths.size());
    }

    public static void assertValidShortestPaths(List<GraphPath<DWGNode, DWGEdge>> paths, List<GraphPath<DWGNode, DWGEdge>> shortestPath) {
        log.debug("Validating shortest path...");

        double miniCost = -1;
        for (GraphPath<DWGNode, DWGEdge> path : shortestPath) {
            double newCost = calcCost(path);
            if (miniCost >= 0) {
                if (newCost < miniCost)
                    throw new RuntimeException("Not shortest path: " + path);
            } else {
                miniCost = newCost;
            }
        }

        for (GraphPath<DWGNode, DWGEdge> path : paths) {
            if (miniCost > calcCost(path))
                throw new RuntimeException("Not shortest path: " + path);
        }
        log.debug("All {} shortest path are valid", shortestPath.size());
    }

    static double calcCost(GraphPath<DWGNode, DWGEdge> path) {
        return path.getEdgeList().stream().mapToDouble(DWGEdge::getWeight).sum();
    }
}
