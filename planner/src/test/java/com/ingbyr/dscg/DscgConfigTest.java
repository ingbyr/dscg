package com.ingbyr.dscg;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.Test;

class DscgConfigTest {

    @Test
    void loadConfig() throws ConfigurationException {
        DscgConfigFile config = new DscgConfigFile();
        System.out.println(config);
    }

}