package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.indicators.Indicator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ingbyr
 */
@AllArgsConstructor
@Slf4j
public class SurvivalSelectorIndicator implements SurvivalSelector {

    private int survivalSize;

    private Indicator indicator;

    @Override
    public List<Individual> filter(List<Individual> pop, List<Individual> offSpring) {

        List<Individual> feasibleIndividuals = Stream.concat(pop.stream(), offSpring.stream())
                .filter(ind -> ind.isFeasible)
                .collect(Collectors.toList());
        log.trace("Feasible individuals size {}", feasibleIndividuals.size());
        feasibleIndividuals.forEach(ind -> log.trace("{}", ind));

        // Recalculate the fitness because individual has feasible solution
        indicator.calcFitness(feasibleIndividuals);

        List<Individual> bestIndividuals = null;
        if (feasibleIndividuals.size() >= survivalSize) {
            // Display all sorted individuals
//            feasibleIndividuals.stream().sorted().forEach(ind -> {
//                log.debug("{}-{}", ind.getId(), ind.getQos());
//            });

            // Choose survival individual from feasible individuals directly if it has enough individuals
            bestIndividuals = feasibleIndividuals.stream().sorted().limit(survivalSize).collect(Collectors.toList());
        } else {
            Queue<Individual> individuals = new PriorityQueue<>(pop.size() + offSpring.size());
            individuals.addAll(pop);
            individuals.addAll(offSpring);

            bestIndividuals = new ArrayList<>(survivalSize);
            for (int i = 0; i < survivalSize; i++) {
                Individual bestInd = individuals.poll();
                bestIndividuals.add(bestInd);
            }
        }
        return bestIndividuals;
    }
}