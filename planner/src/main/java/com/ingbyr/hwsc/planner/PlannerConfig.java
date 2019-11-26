package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;

public interface PlannerConfig {

    Dataset getDataset();

    int getPopulationSize();

    int getOffspringSize();

    int getSurvivalSize();

    double getCrossoverPossibility();

    double getMutationPossibility();

    int getMutationAddStateWeight();

    int getMutationAddConceptWeight();

    int getMutationDelStateWeight();

    int getMutationDelConceptWeight();

    boolean isEnableConcurrentMode();

    boolean isEnableAutoStop();

    int getMaxGen();

    int getAutoStopStep();
}
