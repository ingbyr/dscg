package com.ingbyr.dscg;

import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class HeuristicInfoTest {

    @Test
    void setup() {
        DataSetReader dataSetReader = new XmlDatasetReader(Dataset.wsc2020_01);
        HeuristicInfo h = new HeuristicInfo();
        h.setup(dataSetReader);

        for (int i = 0; i < h.conceptLevel.size(); i++) {
            System.out.println("Time " + i + " concepts " + h.conceptLevel.get(i));
        }
    }
}