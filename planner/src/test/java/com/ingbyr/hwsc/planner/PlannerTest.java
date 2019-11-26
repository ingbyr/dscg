package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.indicators.BinaryIndicator;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerInnerPlannerYashp2;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.Test;

class PlannerTest {

    @Test
    void exec() throws ConfigurationException {
        PlannerLocalConfig config = new PlannerLocalConfig();

        DataSetReader dataSetReader = new XMLDataSetReader(config.dataset);
        dataSetReader.process();

        InnerPlanner innerPlanner = new InnerInnerPlannerYashp2(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        Evaluator evaluator = null;
        if (config.enableConcurrentMode)
            evaluator = EvaluatorGoalDistanceConcurrent.builder().bMax(10).lMax(10).build();
        else
            evaluator = EvaluatorGoalDistance.builder().bMax(10).lMax(10).build();

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        Crossover crossover = new CrossoverSwapState();

        Mutations mutations = new Mutations();
        mutations.addMutation(new MutationAddState(conceptTime, 0), config.mutationAddGoalWeight);
        mutations.addMutation(new MutationAddConcept(conceptTime, 0.5, 0.5), config.mutationAddAtomWeight);
        mutations.addMutation(new MutationDelState(), config.mutationDelGoalWeight);
        mutations.addMutation(new MutationDelConcept(), config.mutationDelAtomWeight);

        SurvivalSelector survivalSelector = new SurvivalSelectorIndicator(config.survivalSize, new BinaryIndicator(2));

        PlannerAnalyzer analyzer = new PlannerAnalyzer();

        Planner planner = Planner.builder()
                .dataSetReader(dataSetReader)
                .config(config)
                .conceptTime(conceptTime)
                .individualGenerator(individualGenerator)
                .innerPlanner(innerPlanner)
                .crossover(crossover)
                .evaluator(evaluator)
                .populationSize(config.populationSize)
                .offspringSize(config.offspringSize)
                .pCross(config.crossoverPossibility)
                .pMut(config.mutationPossibility)
                .mutations(mutations)
                .survivalSelector(survivalSelector)
                .maxGen(config.maxGen)
                .enableMutate(true)
                .enableCrossover(true)
                .analyzer(analyzer)
                .enableConcurrent(config.enableConcurrentMode)
                .stopStep(config.autoStopStep)
                .enableAutoStop(config.enableAutoStop)
                .build();

        planner.exec();
    }
}