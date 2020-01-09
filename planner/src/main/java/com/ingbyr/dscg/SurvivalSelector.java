package com.ingbyr.dscg;

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
public class SurvivalSelector {

    private int survivalSize;

    private Fitness fitness;

    public List<Individual> filter(List<Individual> population, List<Individual> offSpring) {

        List<Individual> pop = Stream.concat(population.stream(), offSpring.stream()).collect(Collectors.toList());
        List<Individual> feasiblePop = pop.stream()
                .filter(ind -> ind.isFeasible)
                .collect(Collectors.toList());
        log.debug("Feasible individuals size {}", feasiblePop.size());

        // Recalculate the fitness because individual is feasible
        fitness.calc(feasiblePop);

        List<Individual> survivalPop;
        if (feasiblePop.size() >= survivalSize) {
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

        return survivalPop;
    }
}
