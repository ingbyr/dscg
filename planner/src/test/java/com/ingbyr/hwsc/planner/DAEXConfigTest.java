package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.planner.DAEXConfig;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.Test;

class DAEXConfigTest {

    @Test
    void loadConfig() throws ConfigurationException {
        DAEXConfig config = new DAEXConfig();
        System.out.println(config);
    }

}