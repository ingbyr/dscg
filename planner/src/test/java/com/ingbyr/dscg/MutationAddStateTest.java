package com.ingbyr.dscg;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

class MutationAddStateTest {

    @Test
    void mutate() {
        DataSetReader dataSetReader = new XmlDatasetReader(Dataset.wsc2009_01);

        HeuristicInfo heuristicInfo = new HeuristicInfo();
        heuristicInfo.setup(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, heuristicInfo);
        Individual individual = individualGenerator.generate(3);
        individual.lastReachedStateIndex = 2;

        Mutation mutation = new MutationAddState(heuristicInfo, 0);
        for (int i = 0; i < 10; i++) {
            mutation.mutate(individual);
        }
    }
}