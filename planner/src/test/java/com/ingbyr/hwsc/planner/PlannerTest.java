package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.XMLDataSetReader;
import org.junit.jupiter.api.Test;

class PlannerTest {

    @Test
    void exec() throws Exception {
        PlannerConfig config = new PlannerConfigFile();
        System.out.println(config);
        Planner planner = new Planner();
        planner.setup(config, new XMLDataSetReader());
        planner.exec();
    }
}