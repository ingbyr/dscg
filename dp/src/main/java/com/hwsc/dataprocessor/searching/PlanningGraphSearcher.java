package com.hwsc.dataprocessor.searching;

import com.hwsc.dataprocessor.models.PlanningGraph;

public interface PlanningGraphSearcher {

    PlanningGraph search();

    long getComposeTime();

    long getBackTrackingTime();

}
