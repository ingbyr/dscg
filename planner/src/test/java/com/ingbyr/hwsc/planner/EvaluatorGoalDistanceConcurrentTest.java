package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerPlannerYashp2;
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

        InnerPlanner innerPlanner = new InnerPlannerYashp2(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        Evaluator evaluator = new EvaluatorGoalDistanceConcurrent();
        evaluator.setMaxStateSize(10);
        evaluator.setInnerPlannerMaxStep(10);

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        Individual individual = individualGenerator.generate(-1);
        List<Individual> individuals = new ArrayList<>();
        individuals.add(individual);
        evaluator.evaluate(individuals, innerPlanner);
        System.out.println(individual);
    }

}