package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.planner.Individual;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Indicator {

    void calculatePopulationFitness(List<Individual> population);

}
