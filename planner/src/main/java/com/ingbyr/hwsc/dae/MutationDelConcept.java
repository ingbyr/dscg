package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.utils.UniformUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ingbyr
 */
@Slf4j
public class MutationDelConcept implements Mutation {
    @Override
    public boolean mutate(Individual individual) {
        int selectedStateIndex = UniformUtils.rangeII(1, Math.min(individual.getStateSize() - 1, individual.lastReachedStateIndex + 1));
        State selectedState = individual.getState(selectedStateIndex);
        if (selectedState.concepts.size() <= 1) {
            log.error("Abort to mutation because of atom size <= 1");
            return false;
        }
        Concept removedConcept = UniformUtils.oneFromSet(selectedState.concepts);
        log.trace("Remove concept {} from {}", removedConcept, selectedState);
        selectedState.concepts.remove(removedConcept);
        log.debug("Create {}", individual);
        return true;
    }
}
