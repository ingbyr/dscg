package com.ingbyr.hwsc.planner;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ingbyr
 */
@Slf4j
public class Mutations {

    private final List<Mutation> ops = new ArrayList<>();

    /**
     * Add mutation to the ops
     *
     * @param mutation Mutation
     * @param weight   Weight
     */
    public void addMutation(Mutation mutation, int weight) {
        for (int i = 0; i < weight; i++) {
            ops.add(mutation);
        }
    }

    /**
     * FIXME Must do one mutation
     * @param individual Individual
     */
    public void mutate(Individual individual) {
        Collections.shuffle(ops);
        log.trace("Mutate {}", individual);
        for (Mutation mutation : ops) {
            if (mutation.mutate(individual))
                break;
        }
    }
}
