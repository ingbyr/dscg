package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.dataset.reader.DataSetReader;
import com.ingbyr.hwsc.dataset.reader.XMLDataSetReader;
import com.ingbyr.hwsc.utils.UniformUtils;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class CrossoverSwapStateTest {

    @Test
    void doCrossover() {
        DataSetReader dataSetReader = new XMLDataSetReader("2009", "01");
        dataSetReader.process();
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