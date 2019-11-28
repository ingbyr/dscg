package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.Individual;
import com.ingbyr.hwsc.planner.IndividualGenerator;
import com.ingbyr.hwsc.planner.Mutation;
import com.ingbyr.hwsc.planner.MutationAddState;
import com.ingbyr.hwsc.planner.ConceptTime;
import org.junit.jupiter.api.Test;

class MutationAddStateTest {

    @Test
    void mutate() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);

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