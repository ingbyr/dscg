package com.ingbyr.hwsc.indicators;

import com.ingbyr.hwsc.dae.Individual;

import java.util.List;

/**
 * @author ingbyr
 */
public interface Indicator {

    void calcFitness(List<Individual> population);

}
