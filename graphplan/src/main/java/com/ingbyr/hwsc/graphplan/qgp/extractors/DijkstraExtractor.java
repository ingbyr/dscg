package com.ingbyr.hwsc.graphplan.qgp.extractors;

import com.google.common.collect.Lists;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGEdge;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import com.ingbyr.hwsc.graphplan.qgp.CPG;

/**
 * @author ingbyr
 */
@Slf4j
public class DijkstraExtractor extends AbstractExtractor implements PlanExtractor {

    private DijkstraShortestPath<DWGNode, DWGEdge> dijkstraAlg;

    public DijkstraExtractor(CPG transfer) {
        super(transfer);
        dijkstraAlg = new DijkstraShortestPath<>(g);
    }

    @Override
    protected void findHelper() {
        GraphPath<DWGNode, DWGEdge> path = dijkstraAlg.getPath(transfer.getStartNode(), transfer.getTargetNode());
        paths = Lists.newArrayList(path);
    }
}
