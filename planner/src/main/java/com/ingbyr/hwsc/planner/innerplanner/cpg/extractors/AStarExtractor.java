package com.ingbyr.hwsc.planner.innerplanner.cpg.extractors;

import com.google.common.collect.Lists;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.DWGNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;

@Slf4j
public class AStarExtractor extends AbstractExtractor implements PlanExtractor {

    @Setter
    private AStarAdmissibleHeuristic<DWGNode> heuristic;

    public AStarExtractor(AStarAdmissibleHeuristic<DWGNode> heuristic) {
        this.name = "a start";
        this.heuristic = heuristic;
    }

    @Override
    protected void findHelper() {
        if (!cpg.isReverseGraph()) throw new RuntimeException("forget to reverse graph?");
        AStarShortestPath<DWGNode, DWGEdge> aStarAlg = new AStarShortestPath<>(g, heuristic);
        GraphPath<DWGNode, DWGEdge> path = aStarAlg.getPath(cpg.getStartNode(), cpg.getTargetNode());
        paths = Lists.newArrayList(path);
    }
}
