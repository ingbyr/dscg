package com.ingbyr.hwsc.planner;

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

    private Fitness fitness;

    @Override
    public List<Individual> filter(List<Individual> population, List<Individual> offSpring) {

        List<Individual> pop = Stream.concat(population.stream(), offSpring.stream()).collect(Collectors.toList());
        List<Individual> feasiblePop = pop.stream()
                .filter(ind -> ind.isFeasible)
                .collect(Collectors.toList());
        log.trace("Feasible individuals size {}", feasiblePop.size());
        feasiblePop.forEach(ind -> log.trace("{}", ind));

        // Recalculate the fitness because individual has feasible solution
        fitness.calculatePopulationFitness(feasiblePop);

        List<Individual> survivalPop = null;
        if (feasiblePop.size() >= survivalSize) {

//            // TODO for debug=========
//            List<Individual> tmp = feasibleIndividuals.stream().sorted().collect(Collectors.toList());
//            for (Individual individual : tmp) {
//                log.debug("{} {}", individual.getFitness(), individual.getQos());
//            }
//            // end debug==============

            // Choose survival individual from feasible individuals directly if it has enough individuals
            survivalPop = feasiblePop.stream().sorted().limit(survivalSize).collect(Collectors.toList());
        } else {
            Queue<Individual> individuals = new PriorityQueue<>(pop.size() + offSpring.size());
            individuals.addAll(pop);
            individuals.addAll(offSpring);

            survivalPop = new ArrayList<>(survivalSize);
            for (int i = 0; i < survivalSize && individuals.peek() != null; i++) {
                Individual bestInd = individuals.poll();
                survivalPop.add(bestInd);
            }
        }

        // Add protected individuals to survival population
        List<Individual> protectedPop = pop.stream().filter(Individual::isAlive).collect(Collectors.toList());
        survivalPop.addAll(protectedPop);

        return survivalPop;
    }
}
