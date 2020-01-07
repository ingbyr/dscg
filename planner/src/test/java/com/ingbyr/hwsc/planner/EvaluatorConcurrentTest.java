package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerPlannerYashp2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ingbyr
 */
class EvaluatorConcurrentTest {

    @Test
    void evaluate() {
        DataSetReader dataSetReader = new XmlDatasetReader(Dataset.wsc2009_01);

        InnerPlanner innerPlanner = new InnerPlannerYashp2(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        Evaluate evaluate = new EvaluatorConcurrent();
        evaluate.setMaxStateSize(10);
        evaluate.setInnerPlannerMaxStep(10);

        HeuristicInfo heuristicInfo = new HeuristicInfo();
        heuristicInfo.setup(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, heuristicInfo);

        Individual individual = individualGenerator.generate(-1);
        List<Individual> individuals = new ArrayList<>();
        individuals.add(individual);
        evaluate.evaluate(individuals, innerPlanner);
        System.out.println(individual);
    }

}