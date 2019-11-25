package com.ingbyr.hwsc.planner.planner.cpg.extractors;


import com.ingbyr.hwsc.planner.planner.cpg.models.CompletePlaningGraph;
import com.ingbyr.hwsc.planner.planner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.planner.cpg.models.DWGNode;
import org.jgrapht.GraphPath;

import java.util.List;

/**
 * @author ingbyr
 */
public interface PlanExtractor {
    List<GraphPath<DWGNode, DWGEdge>> getPaths();

    double find();

    void setName(String name);

    void setCpg(CompletePlaningGraph cpg);

    int getSteps();
}
