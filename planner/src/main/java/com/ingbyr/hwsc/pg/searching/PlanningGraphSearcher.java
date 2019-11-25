package com.ingbyr.hwsc.pg.searching;

import com.ingbyr.hwsc.model.PlanningGraph;

public interface PlanningGraphSearcher {

    boolean search();

    PlanningGraph getPlanningGraph();

    long getComposeTime();

    long getBackTrackingTime();

}
