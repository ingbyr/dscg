package com.hwsc.extractors;


import com.hwsc.models.DWGEdge;
import com.hwsc.models.DWGNode;
import org.jgrapht.GraphPath;

import java.util.List;

/**
 * @author ingbyr
 */
public interface PlanExtractor {
    List<GraphPath<DWGNode, DWGEdge>> getPaths();

    double find();

}
