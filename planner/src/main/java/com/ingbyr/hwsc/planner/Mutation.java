package com.ingbyr.hwsc.planner;

@FunctionalInterface
public interface Mutation {
    /**
     * Mutate the individual
     *
     * @param individual Individual
     * @return If mutation is successful, return true.
     */
    boolean mutate(Individual individual);
}
