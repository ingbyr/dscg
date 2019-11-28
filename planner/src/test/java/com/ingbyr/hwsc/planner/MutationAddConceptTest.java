package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.Individual;
import com.ingbyr.hwsc.planner.IndividualGenerator;
import com.ingbyr.hwsc.planner.Mutation;
import com.ingbyr.hwsc.planner.MutationAddConcept;
import com.ingbyr.hwsc.planner.ConceptTime;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class MutationAddConceptTest {

    @Test
    void mutate() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);
        Individual individual = individualGenerator.generate(3);
        individual.lastReachedStateIndex = 2;

        Mutation mutation = new MutationAddConcept(conceptTime, 0.5, 0.5);
        mutation.mutate(individual);
    }
}