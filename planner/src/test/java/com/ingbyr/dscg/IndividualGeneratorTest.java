package com.ingbyr.dscg;

import com.ingbyr.dscg.utils.UniformUtils;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

class IndividualGeneratorTest {

    @Test
    void generate() {
        DataSetReader dataSetReader = new XmlDatasetReader(Dataset.wsc2009_01);

        HeuristicInfo heuristicInfo = new HeuristicInfo();
        heuristicInfo.setup(dataSetReader);
        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, heuristicInfo);

        // Generate population
        int candidateStartTimesSize = heuristicInfo.candidateStartTimes.length;
        for (int i = 0; i < 20; i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, candidateStartTimesSize);
            individualGenerator.generate(randomTimeSize);
        }
    }
}