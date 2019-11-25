package com.ingbyr.hwsc.planner.cpg.extractors;

import com.google.common.collect.Lists;
import com.ingbyr.hwsc.planner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.cpg.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * @author ingbyr
 */
@Slf4j
public class DijkstraExtractor extends AbstractExtractor implements PlanExtractor {

    public DijkstraExtractor() {
        this.name = "dijkstra";
    }

    @Override
    protected void findHelper() {
        DijkstraShortestPath<DWGNode, DWGEdge> dijkstraAlg = new DijkstraShortestPath<>(g);
        GraphPath<DWGNode, DWGEdge> path = dijkstraAlg.getPath(cpg.getStartNode(), cpg.getTargetNode());
        paths = Lists.newArrayList(path);
        // TODO Steps can not be calculated in this alg
        steps = path.getLength();
    }
}
