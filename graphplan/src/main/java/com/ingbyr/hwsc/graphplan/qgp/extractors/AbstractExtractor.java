package com.ingbyr.hwsc.graphplan.qgp.extractors;

import com.ingbyr.hwsc.graphplan.qgp.CPG;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGEdge;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;

/**
 * @author ingbyr
 */
@Slf4j
public abstract class AbstractExtractor implements PlanExtractor {

    CPG transfer;

    Graph<DWGNode, DWGEdge> g;

    @Getter
    protected List<GraphPath<DWGNode, DWGEdge>> paths;

    AbstractExtractor(CPG transfer) {
        this.transfer = transfer;
        this.g = this.transfer.getDwGraph();
    }

    protected abstract void findHelper();

    @Override
    public double find() {
        long start = System.currentTimeMillis();
        findHelper();
        long end = System.currentTimeMillis();
        log.debug("Found paths in {} ms", end - start);
        return end - start;
    }
}
