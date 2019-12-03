package com.hwsc.baseline.cpg.extractors;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import com.hwsc.baseline.cpg.models.DWGNode;

@Slf4j
public class HSPRHeuristic<V extends DWGNode> implements AStarAdmissibleHeuristic<V> {

    private boolean isDij;

    public HSPRHeuristic(boolean isDij) {
        this.isDij = isDij;
    }

    @Override
    public double getCostEstimate(V sourceVertex, V targetVertex) {
        if (isDij)
            return 0.0;
        else
            return sourceVertex.getDistance();
    }

    @Override
    public <E> boolean isConsistent(Graph<V, E> graph) {
        return true;
    }
}