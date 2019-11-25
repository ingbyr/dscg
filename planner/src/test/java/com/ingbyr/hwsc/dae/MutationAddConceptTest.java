package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.reader.DataSetReader;
import com.ingbyr.hwsc.reader.XMLDataSetReader;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class MutationAddConceptTest {

    @Test
    void mutate() {
        DataSetReader dataSetReader = new XMLDataSetReader("2009", "01");
        dataSetReader.process();

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);
        Individual individual = individualGenerator.generate(3);
        individual.lastReachedStateIndex = 2;

        Mutation mutation = new MutationAddConcept(conceptTime, 0.5, 0.5);
        mutation.mutate(individual);
    }
}