package com.hwsc.extractors;

import com.hwsc.models.DWGEdge;
import com.hwsc.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import com.hwsc.models.CompletePlaningGraph;

/**
 * @author ingbyr
 */
@Slf4j
public class AllPathsExtractor extends AbstractExtractor implements PlanExtractor {

    private AllDirectedPaths<DWGNode, DWGEdge> allPathsAlg;

    public AllPathsExtractor(CompletePlaningGraph transfer) {
        super(transfer);
        allPathsAlg = new AllDirectedPaths<>(g);
    }

    @Override
    protected void findHelper() {
        paths = allPathsAlg.getAllPaths(transfer.getStartNode(), transfer.getTargetNode(), true, null);
    }
}
