package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XMLDataSetReader;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class CrossoverSwapStateTest {

    @Test
    void doCrossover() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        HeuristicInfo heuristicInfo = new HeuristicInfo();
        heuristicInfo.setup(dataSetReader);
        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, heuristicInfo);

        // Generate population
        List<Individual> population = new LinkedList<>();
        int candidateStartTimesSize = heuristicInfo.candidateStartTimes.length;
        for (int i = 0; i < 100; i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, candidateStartTimesSize);
            population.add(individualGenerator.generate(randomTimeSize));
        }

        Crossover crossover = new CrossoverSwapState();

        for (int i = 0; i < 100; i++) {
            // Create new individual
            Individual individual1 = UniformUtils.oneFromList(population);
            Individual individual2 = UniformUtils.oneFromList(population);

            // Crossover
            Individual newIndividual = crossover.doCrossover(individual1, individual2);
        }
    }
}