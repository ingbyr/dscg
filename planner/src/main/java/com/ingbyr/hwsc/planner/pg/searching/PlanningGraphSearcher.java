package com.ingbyr.hwsc.planner.pg.searching;

import com.ingbyr.hwsc.planner.model.PlanningGraph;

public interface PlanningGraphSearcher {

    boolean search();

    PlanningGraph getPlanningGraph();

    long getComposeTime();

    long getBackTrackingTime();

}
