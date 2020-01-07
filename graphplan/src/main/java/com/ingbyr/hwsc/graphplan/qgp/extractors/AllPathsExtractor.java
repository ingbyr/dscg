package com.ingbyr.hwsc.graphplan.qgp.extractors;

import com.ingbyr.hwsc.graphplan.qgp.CPG;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGEdge;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

/**
 * @author ingbyr
 */
@Slf4j
public class AllPathsExtractor extends AbstractExtractor implements PlanExtractor {

    private AllDirectedPaths<DWGNode, DWGEdge> allPathsAlg;

    public AllPathsExtractor(CPG transfer) {
        super(transfer);
        allPathsAlg = new AllDirectedPaths<>(g);
    }

    @Override
    protected void findHelper() {
        paths = allPathsAlg.getAllPaths(transfer.getStartNode(), transfer.getTargetNode(), true, null);
    }
}
