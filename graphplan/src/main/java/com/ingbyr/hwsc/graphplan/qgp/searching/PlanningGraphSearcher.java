package com.ingbyr.hwsc.graphplan.qgp.searching;

import com.ingbyr.hwsc.graphplan.qgp.models.PlanningGraph;

public interface PlanningGraphSearcher {

    PlanningGraph search();

    long getComposeTime();

    long getBackTrackingTime();

}
