package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.reader.DataSetReader;
import com.ingbyr.hwsc.reader.XMLDataSetReader;
import com.ingbyr.hwsc.utils.UniformUtils;
import org.junit.jupiter.api.Test;

class IndividualGeneratorTest {

    @Test
    void generate() {
        DataSetReader dataSetReader = new XMLDataSetReader("2009", "01");
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