package com.ingbyr.hwsc.planner;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.Test;

class PlannerConfigTest {

    @Test
    void loadConfig() throws ConfigurationException {
        PlannerLocalConfig config = new PlannerLocalConfig();
        System.out.println(config);
    }

}