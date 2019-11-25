package com.ingbyr.hwsc.dae;

import java.util.List;

/**
 * @author ingbyr
 */
public interface SurvivalSelector {
    List<Individual> filter(List<Individual> pop, List<Individual> offSpring);
}
