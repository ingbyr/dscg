package com.hwsc.baseline.cpg.extractors;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import com.hwsc.baseline.cpg.models.CompletePlaningGraph;
import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;

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
