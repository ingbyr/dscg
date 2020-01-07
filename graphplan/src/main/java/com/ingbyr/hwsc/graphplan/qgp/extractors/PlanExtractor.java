package com.ingbyr.hwsc.graphplan.qgp.extractors;


import com.ingbyr.hwsc.graphplan.qgp.models.DWGEdge;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGNode;
import org.jgrapht.GraphPath;

import java.util.List;

/**
 * @author ingbyr
 */
public interface PlanExtractor {
    List<GraphPath<DWGNode, DWGEdge>> getPaths();

    double find();

}
