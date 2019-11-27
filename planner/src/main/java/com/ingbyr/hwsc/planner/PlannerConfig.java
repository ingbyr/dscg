package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class PlannerConfig {

    Dataset dataset;

    int populationSize;

    int offspringSize;

    int survivalSize;

    double crossoverPossibility;

    double mutationPossibility;

    int mutationAddStateWeight;

    int mutationAddConceptWeight;

    int mutationDelStateWeight;

    int mutationDelConceptWeight;

    boolean enableConcurrentMode;

    boolean enableAutoStop;

    int maxGen;

    int autoStopStep;
}
