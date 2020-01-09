package com.ingbyr.dscg;

import com.ingbyr.dscg.utils.UniformUtils;
import com.ingbyr.hwsc.common.Concept;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class MutationAddState implements Mutation {

    private HeuristicInfo heuristicInfo;

    private int radius;

    @Override
    public boolean mutate(Individual individual) {

        int selectedStateIndex = UniformUtils.rangeII(0, Math.min(individual.getStateSize() - 1, individual.lastReachedStateIndex + 1));
        int t1 = individual.getState(selectedStateIndex).earliestTime;
        int t2 = individual.getState(selectedStateIndex + 1).earliestTime;
        log.trace("Mutate at {} of {}", selectedStateIndex, individual);

        if (t2 == t1 + 1 || t2 <= t1) {
            log.debug("Mutation is aborted because of state[{}] is next to state[{}]", t1, t2);
            return false;
        }

        int t = UniformUtils.rangeEE(t1, t2);
        State newState = neighbourhoodState(t);
        if (newState == null)
            return false;

        log.trace("Insert new state {} at {}", newState, selectedStateIndex + 1);
        individual.addState(selectedStateIndex + 1, newState);
        log.debug("Create {}", individual);
        return true;
    }

    private State neighbourhoodState(int t) {
        int l = Math.max(1, t - (2 * radius + 1));
        int r = Math.min(heuristicInfo.time, t + (2 * radius + 1));
        log.trace("Mutate selected time is {} , and select concepts in time [{}, {}]", t, l, r);

        if (l == r) {
            log.debug("Mutation is aborted because of no available concepts");
            return null;
        }

        Set<Concept> neighbourhoodConcepts = new HashSet<>();
        for (int i = l; i < r; i++) {
            neighbourhoodConcepts.addAll(heuristicInfo.conceptLevel.get(i));
        }

        int stateSize = UniformUtils.rangeIE(1, neighbourhoodConcepts.size());
        Set<Concept> concepts = UniformUtils.set(neighbourhoodConcepts, stateSize);

        int newTime = t;
        for (Concept concept : concepts) {
            newTime = Math.max(heuristicInfo.earliestTimeOfConcept.get(concept), newTime);
        }

        return new State(concepts, newTime);
    }
}
