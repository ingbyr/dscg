package com.hwsc.baseline.cpg.extractors;


import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;
import org.jgrapht.GraphPath;

import java.util.List;

/**
 * @author ingbyr
 */
public interface PlanExtractor {
    List<GraphPath<DWGNode, DWGEdge>> getPaths();

    double find();

}
