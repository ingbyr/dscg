package com.ingbyr.hwsc.planner.cpg.extractors;

import com.ingbyr.hwsc.planner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.cpg.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

/**
 * @author ingbyr
 */
@Slf4j
public class AllPathsExtractor extends AbstractExtractor implements PlanExtractor {

    public AllPathsExtractor() {
        this.name = "all paths";

    }

    @Override
    protected void findHelper() {
        AllDirectedPaths<DWGNode, DWGEdge> allPathsAlg = new AllDirectedPaths<>(g);
        paths = allPathsAlg.getAllPaths(cpg.getStartNode(), cpg.getTargetNode(), true, null);
    }
}
