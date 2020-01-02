package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XMLDataSetReader;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class HeuristicInfoTest {

    @Test
    void build() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        HeuristicInfo heuristicInfo = new HeuristicInfo();
        heuristicInfo.setup(dataSetReader);
    }

    @Test
    void setup() {
    }

    @Test
    void update() {
    }
}