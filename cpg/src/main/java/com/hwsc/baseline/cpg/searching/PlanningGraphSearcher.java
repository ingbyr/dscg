package com.hwsc.baseline.cpg.searching;

import com.hwsc.baseline.cpg.models.PlanningGraph;

public interface PlanningGraphSearcher {

    PlanningGraph search();

    long getComposeTime();

    long getBackTrackingTime();

}
