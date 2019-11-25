package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.*;
import com.ingbyr.hwsc.planner.ConceptTime;
import com.ingbyr.hwsc.planner.planner.yashp2.Yashp2Planner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ingbyr
 */
class EvaluatorGoalDistanceConcurrentTest {

    @Test
    void evaluate() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        dataSetReader.process();

        Planner planner = new Yashp2Planner(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        Evaluator evaluator = EvaluatorGoalDistance.builder().lMax(10).bMax(10).build();

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        Individual individual = individualGenerator.generate(-1);
        List<Individual> individuals = new ArrayList<>();
        individuals.add(individual);
        evaluator.evaluate(individuals, planner);
        System.out.println(individual);
    }

}