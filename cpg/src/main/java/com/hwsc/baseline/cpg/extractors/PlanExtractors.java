package com.hwsc.baseline.cpg.extractors;

import com.hwsc.baseline.cpg.models.CompletePlaningGraph;
import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public final class PlanExtractors {

    private PlanExtractors() {
    }

    public static void assertValidPaths(List<GraphPath<DWGNode, DWGEdge>> paths) {
        log.debug("validating paths ...");
        for (GraphPath<DWGNode, DWGEdge> path : paths) {

            Set<Concept> output = path.getStartVertex().getOutputConcepts();

            for (DWGEdge edge : path.getEdgeList()) {
                if (!output.containsAll(CompletePlaningGraph.mergeInputConcepts(edge.getServices())))
                    throw new RuntimeException("not valid edge: " + edge);
                output.addAll(CompletePlaningGraph.mergeOutputConcepts(edge.getServices()));
            }
        }
        log.debug("all {} path are valid", paths.size());
    }

    public static void assertValidShortestPaths(List<GraphPath<DWGNode, DWGEdge>> paths, List<GraphPath<DWGNode, DWGEdge>> shortestPath) {
        log.debug("validating shortest path...");

        double miniCost = -1;
        for (GraphPath<DWGNode, DWGEdge> path : shortestPath) {
            double newCost = calcCost(path);
            if (miniCost >= 0) {
                if (newCost < miniCost)
                    throw new RuntimeException("not shortest path: " + path);
            } else {
                miniCost = newCost;
            }
        }

        for (GraphPath<DWGNode, DWGEdge> path : paths) {
            if (miniCost > calcCost(path))
                throw new RuntimeException("not shortest path: " + path);
        }
        log.debug("all {} shortest path are valid", shortestPath.size());
    }

    public static double calcCost(GraphPath<DWGNode, DWGEdge> path) {
        return path.getEdgeList().stream().mapToDouble(DWGEdge::getWeight).sum();
    }

    public static List<Service> getServices(GraphPath<DWGNode, DWGEdge> path) {
        List<Service> services = new LinkedList<>();
        for (DWGEdge edge : path.getEdgeList()) {
            edge.getServices().forEach(ser -> {
                Service s = ser.getService();
                if (!"target".equals(s.getName()) && !"start".equals(s.getName()))
                    services.add(s);
            });
        }
        return services;
    }

    public static boolean validServices(List<Service> services, Set<Concept> in, Set<Concept> goal) {
        boolean isValid = false;
        Set<Concept> concepts = new HashSet<>(in);
        for (Service service : services) {
            isValid = concepts.containsAll(service.getInputConceptSet());
            if (!isValid) break;
            else {
                concepts.addAll(service.getOutputConceptSet());
            }
        }
        isValid = concepts.containsAll(goal);
        return isValid;
    }
}
