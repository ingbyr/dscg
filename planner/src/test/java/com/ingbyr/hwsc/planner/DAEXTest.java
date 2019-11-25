package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.*;
import com.ingbyr.hwsc.planner.ConceptTime;
import com.ingbyr.hwsc.planner.indicators.BinaryIndicator;
import com.ingbyr.hwsc.planner.planner.yashp2.Yashp2Planner;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.Test;

class DAEXTest {

    @Test
    void exec() throws ConfigurationException {
        DAEXConfig config = new DAEXConfig();

        DataSetReader dataSetReader = new XMLDataSetReader(config.dataset);
        dataSetReader.process();

        Planner planner = new Yashp2Planner(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        Evaluator evaluator = null;
        if (config.enableConcurrent)
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

        DAEAnalyzer analyzer = new DAEAnalyzer();

        DAEX daex = DAEX.builder()
                .dataSetReader(dataSetReader)
                .config(config)
                .conceptTime(conceptTime)
                .individualGenerator(individualGenerator)
                .planner(planner)
                .crossover(crossover)
                .evaluator(evaluator)
                .populationSize(config.populationSize)
                .offspringSize(config.offspringSize)
                .pCross(config.pCross)
                .pMut(config.pMut)
                .mutations(mutations)
                .survivalSelector(survivalSelector)
                .maxGen(config.maxGen)
                .enableMutate(true)
                .enableCrossover(true)
                .analyzer(analyzer)
                .enableConcurrent(config.enableConcurrent)
                .stopStep(config.stopStep)
                .enableAutoStop(config.enableAutoStop)
                .build();

        daex.exec();
    }
}