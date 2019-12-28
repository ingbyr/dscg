package com.hwsc.dataprocessor.extractors;


import com.hwsc.dataprocessor.models.DWGEdge;
import com.hwsc.dataprocessor.models.DWGNode;
import org.jgrapht.GraphPath;

import java.util.List;

/**
 * @author ingbyr
 */
public interface PlanExtractor {
    List<GraphPath<DWGNode, DWGEdge>> getPaths();

    double find();

}
