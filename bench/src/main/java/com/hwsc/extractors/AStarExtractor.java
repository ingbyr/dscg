package com.hwsc.extractors;

import com.google.common.collect.Lists;
import com.hwsc.models.CompletePlaningGraph;
import com.hwsc.models.DWGEdge;
import com.hwsc.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;

@Slf4j
public class AStarExtractor extends AbstractExtractor implements PlanExtractor {

    private AStarShortestPath<DWGNode, DWGEdge> aStarAlg;

    public AStarExtractor(CompletePlaningGraph transfer, AStarAdmissibleHeuristic<DWGNode> heuristic) {
        super(transfer);
        if (!transfer.isReverseGraph()) throw new RuntimeException("forget to reverse graph?");
        this.aStarAlg = new AStarShortestPath<>(g, heuristic);
    }


    @Override
    protected void findHelper() {
        GraphPath<DWGNode, DWGEdge> path = aStarAlg.getPath(transfer.getStartNode(), transfer.getTargetNode());
        paths = Lists.newArrayList(path);
    }
}
