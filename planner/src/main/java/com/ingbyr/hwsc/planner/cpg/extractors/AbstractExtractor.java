package com.ingbyr.hwsc.planner.cpg.extractors;

import com.ingbyr.hwsc.planner.cpg.models.CompletePlaningGraph;
import com.ingbyr.hwsc.planner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.cpg.models.DWGNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Objects;

import static com.ingbyr.hwsc.planner.cpg.extractors.PlanExtractors.calcCost;

/**
 * @author ingbyr
 */
@Slf4j
public abstract class AbstractExtractor implements PlanExtractor {

    @Setter
    CompletePlaningGraph cpg;

    Graph<DWGNode, DWGEdge> g;

    @Getter
    protected List<GraphPath<DWGNode, DWGEdge>> paths;

    @Getter
    @Setter
    protected String name;

    @Getter
    int steps;

    AbstractExtractor() {
    }

    protected abstract void findHelper();

    @Override
    public double find() {
        Objects.requireNonNull(cpg);
        g = cpg.getDwGraph();
        log.debug("Search path by {}", name);
        long start = System.currentTimeMillis();
        findHelper();
        long end = System.currentTimeMillis();
        log.debug("Used {} ms to find path", end - start);

        if (paths.size() > 1) {
            log.debug("Find all {} path", paths.size());
        } else {
            paths.forEach(path -> {
                log.debug("Shortest path is {}", path);
                log.debug("Cost is {}", calcCost(path));
            });
        }

        return end - start;
    }
}
