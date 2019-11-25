package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.reader.DataSetReader;
import com.ingbyr.hwsc.reader.XMLDataSetReader;
import org.junit.jupiter.api.Test;

class MutationAddStateTest {

    @Test
    void mutate() {
        DataSetReader dataSetReader = new XMLDataSetReader("2009", "01");
        dataSetReader.process();

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);
        Individual individual = individualGenerator.generate(3);
        individual.lastReachedStateIndex = 2;

        Mutation mutation = new MutationAddState(conceptTime, 0);
        for (int i = 0; i < 10; i++) {
            mutation.mutate(individual);
        }
    }
}