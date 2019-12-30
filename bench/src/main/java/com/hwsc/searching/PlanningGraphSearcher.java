package com.hwsc.searching;

import com.hwsc.models.PlanningGraph;

public interface PlanningGraphSearcher {

    PlanningGraph search();

    long getComposeTime();

    long getBackTrackingTime();

}
