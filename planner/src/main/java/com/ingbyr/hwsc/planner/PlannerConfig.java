package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import lombok.*;

@Data
@NoArgsConstructor
public class PlannerConfig {

    public static final String EVALUATOR_CLASS_PREFIX = "com.ingbyr.hwsc.planner.";

    public static final String INDICATOR_CLASS_PREFIX = "com.ingbyr.hwsc.planner.";

    protected Dataset dataset;

    protected int populationSize;

    protected int offspringSize;

    protected int survivalSize;

    protected double crossoverPossibility;

    protected double mutationPossibility;

    protected int mutationAddStateWeight;

    protected int mutationAddConceptWeight;

    protected int mutationDelStateWeight;

    protected int mutationDelConceptWeight;

    protected boolean enableAutoStop;

    protected int maxGen;

    protected int autoStopStep;

    protected boolean saveToFile;

    // Mutation config
    protected int mutationAddStateRadius;

    protected double mutationAddConceptAddPossibility;

    protected double mutationAddConceptChangePossibility;

    // Evaluator config
    protected String evaluator;

    // Indicator config
    protected String indicator;

    protected int innerPlanMaxStep;

    protected int maxStateSize;

}
