package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ingbyr
 */
@Slf4j
@NoArgsConstructor
public class MutationDelState implements Mutation {
    @Override
    public boolean mutate(Individual individual) {
        if (individual.getStateSize() <= 3) {
            log.warn("No available state to remove {}", individual);
            return false;
        }
        int selectedStateIndex = UniformUtils.rangeII(1, Math.min(individual.getStateSize() - 2, individual.lastReachedStateIndex + 1));
        State state = individual.removeState(selectedStateIndex);
        log.trace("Remove state {}", state);
        log.debug("Create {}", individual);
        return true;
    }
}
