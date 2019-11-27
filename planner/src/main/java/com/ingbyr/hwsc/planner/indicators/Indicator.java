package com.ingbyr.hwsc.planner.indicators;

import com.ingbyr.hwsc.planner.Individual;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Indicator {

    void calculatePopulationFitness(List<Individual> population);

}
