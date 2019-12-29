package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XMLDataSetReader;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class MutationDelConceptTest {

    @Test
    void mutate() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);

        Context context = new Context();
        context.setup(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, context);
        Individual individual = individualGenerator.generate(3);
        individual.lastReachedStateIndex = 2;

        Mutation mutation = new MutationDelConcept();
        mutation.mutate(individual);
    }
}