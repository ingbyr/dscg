package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

class PlannerTest {

    @Test
    void exec() throws Exception {
        PlannerConfig config = new PlannerConfigFile();
        Planner planner = new Planner();
        planner.setup(config, new XmlDatasetReader());
        planner.exec();
    }
}