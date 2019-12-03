package com.hwsc.baseline.cpg.extractors;

import com.google.common.collect.Lists;
import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import com.hwsc.baseline.cpg.models.CompletePlaningGraph;

/**
 * @author ingbyr
 */
@Slf4j
public class DijkstraExtractor extends AbstractExtractor implements PlanExtractor {

    private DijkstraShortestPath<DWGNode, DWGEdge> dijkstraAlg;

    public DijkstraExtractor(CompletePlaningGraph transfer) {
        super(transfer);
        dijkstraAlg = new DijkstraShortestPath<>(g);
    }

    @Override
    protected void findHelper() {
        GraphPath<DWGNode, DWGEdge> path = dijkstraAlg.getPath(transfer.getStartNode(), transfer.getTargetNode());
        paths = Lists.newArrayList(path);
    }
}
