package com.ingbyr.dscg;

import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

class DscgTest {

    @Test
    void exec() throws Exception {
        DscgConfig config = new DscgConfigFile();
        Dscg DSCG = new Dscg();
        DSCG.setup(config, new XmlDatasetReader());
        DSCG.exec();
    }
}