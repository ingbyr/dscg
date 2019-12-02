package com.ingbyr.hwsc.planner.pg.searching;

import com.ingbyr.hwsc.planner.innerplanner.cpg.models.PlanningGraph;

public interface PlanningGraphSearcher {

    boolean search();

    PlanningGraph getPlanningGraph();

    long getComposeTime();

    long getBackTrackingTime();

}
