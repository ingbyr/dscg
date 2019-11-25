package com.ingbyr.hwsc.planner;

import java.util.List;

/**
 * @author ingbyr
 */
public interface SurvivalSelector {
    List<Individual> filter(List<Individual> pop, List<Individual> offSpring);
}
