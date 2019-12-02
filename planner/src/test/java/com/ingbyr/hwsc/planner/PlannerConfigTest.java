package com.ingbyr.hwsc.planner;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.Test;

class PlannerConfigTest {

    @Test
    void loadConfig() throws ConfigurationException {
        PlannerConfigFile config = new PlannerConfigFile();
        System.out.println(config);
    }

}