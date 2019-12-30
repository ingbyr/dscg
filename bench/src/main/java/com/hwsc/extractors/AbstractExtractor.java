package com.hwsc.extractors;

import com.hwsc.models.DWGEdge;
import com.hwsc.models.DWGNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import com.hwsc.models.CompletePlaningGraph;

import java.util.List;

/**
 * @author ingbyr
 */
@Slf4j
public abstract class AbstractExtractor implements PlanExtractor {

    CompletePlaningGraph transfer;

    Graph<DWGNode, DWGEdge> g;

    @Getter
    protected List<GraphPath<DWGNode, DWGEdge>> paths;

    AbstractExtractor(CompletePlaningGraph transfer) {
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
