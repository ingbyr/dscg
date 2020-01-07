package com.ingbyr.hwsc.graphplan.qgp.extractors;

import com.google.common.collect.Lists;
import com.ingbyr.hwsc.graphplan.qgp.CPG;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGEdge;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;

@Slf4j
public class AStarExtractor extends AbstractExtractor implements PlanExtractor {

    private AStarShortestPath<DWGNode, DWGEdge> aStarAlg;

    public AStarExtractor(CPG transfer, AStarAdmissibleHeuristic<DWGNode> heuristic) {
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
