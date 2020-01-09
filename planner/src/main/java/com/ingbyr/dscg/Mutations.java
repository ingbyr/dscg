package com.ingbyr.dscg;

import com.ingbyr.dscg.utils.UniformUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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

    public void mutate(Individual individual) {
        Mutation mutation = UniformUtils.oneFromList(ops);
        log.trace("[{}] Apply mutation {} to {}", individual.getId(), mutation.getClass().getSimpleName(), individual);
        mutation.mutate(individual);
    }
}
