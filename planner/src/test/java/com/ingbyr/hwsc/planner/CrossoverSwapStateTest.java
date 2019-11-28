package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.Crossover;
import com.ingbyr.hwsc.planner.CrossoverSwapState;
import com.ingbyr.hwsc.planner.Individual;
import com.ingbyr.hwsc.planner.IndividualGenerator;
import com.ingbyr.hwsc.planner.ConceptTime;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class CrossoverSwapStateTest {

    @Test
    void doCrossover() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);
        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        // Generate population
        List<Individual> population = new LinkedList<>();
        int candidateStartTimesSize = conceptTime.candidateStartTimes.length;
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