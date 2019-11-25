package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.dataset.reader.DataSetReader;
import com.ingbyr.hwsc.dataset.reader.XMLDataSetReader;
import com.ingbyr.hwsc.planner.yashp2.Yashp2Planner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ingbyr
 */
class EvaluatorGoalDistanceConcurrentTest {

    @Test
    void evaluate() {
        DataSetReader dataSetReader = new XMLDataSetReader("2009", "01");
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