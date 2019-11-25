package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.IndividualGenerator;
import com.ingbyr.hwsc.planner.ConceptTime;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import org.junit.jupiter.api.Test;

class IndividualGeneratorTest {

    @Test
    void generate() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        dataSetReader.process();
        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);
        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        // Generate population
        int candidateStartTimesSize = conceptTime.candidateStartTimes.length;
        for (int i = 0; i < 20; i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, candidateStartTimesSize);
            individualGenerator.generate(randomTimeSize);
        }
    }
}