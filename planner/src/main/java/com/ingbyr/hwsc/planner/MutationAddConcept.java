package com.ingbyr.hwsc.planner;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.planner.model.State;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author ingbyr
 */
@AllArgsConstructor
@Slf4j
public class MutationAddConcept implements Mutation {

    ConceptTime conceptTime;

    double pChange;

    double pAdd;

    @Override
    public boolean mutate(Individual individual) {
        int lastChangedStateIndex = Math.min(individual.getStateSize(), individual.lastReachedStateIndex + 1);
        double stepPChange = pChange / (double) individual.getStateSize();

        boolean mutated = false;
        for (int t = 1; t <= lastChangedStateIndex; t++) {
            if (UniformUtils.p() < stepPChange) {
                mutated = changeAtom(individual, t);
            }

            if (UniformUtils.p() < pAdd) {
                mutated = addAtom(individual, t);
            }
        }
        if (mutated)
            log.debug("Create {}", individual);
        else {
            log.warn("Can not add/change concept of {}", individual);
        }
        return mutated;
    }

    private boolean changeAtom(Individual individual, int t) {
        State indState = individual.getState(t);
        Concept removedConcept = UniformUtils.oneFromSet(indState.concepts);
        log.debug("Remove concept {}", removedConcept);
        indState.concepts.remove(removedConcept);
        return addRandomConcepts(indState, t);
    }

    private boolean addAtom(Individual individual, int t) {
        log.debug("Try to add concept to {}", individual);
        State indState = individual.getState(t);
        return addRandomConcepts(indState, t);
    }

    private boolean addRandomConcepts(State state, int t) {
        Set<Concept> remainingConcepts = Sets.difference(conceptTime.conceptsAtTime.get(t), state.concepts);
        if (remainingConcepts.size() == 0) {
            log.warn("Abort to mutation because that all concepts are included");
            return false;
        }
        Concept newConcept = UniformUtils.oneFromSet(remainingConcepts);
        log.trace("Add concept {} in time {}", newConcept, state);
        state.concepts.add(newConcept);
        return true;
    }
}
