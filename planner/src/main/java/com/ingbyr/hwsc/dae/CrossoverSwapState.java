package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.utils.UniformUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CrossoverSwapState implements Crossover {

    @Override
    public Individual doCrossover(Individual indA, Individual indB) {
        Individual newInd = new Individual();

        // Skip input state and goal state
        int stateAIndex = UniformUtils.rangeIE(1, indA.getStateSize() - 1);
        int stateBIndex = UniformUtils.rangeIE(1, indB.getStateSize() - 1);

        int earliestTimeA = indA.getState(stateAIndex).earliestTime;
        int earliestTimeB = indB.getState(stateBIndex).earliestTime;

        if (earliestTimeB > earliestTimeA) {
            for (int i = 0; i <= stateAIndex; i++)
                newInd.addState(indA.getState(i));
            for (int i = stateBIndex; i < indB.getStateSize(); i++)
                newInd.addState(indB.getState(i));
        } else if (earliestTimeA == earliestTimeB){  // Avoid to add same time state
            for (int i = 0; i <= stateBIndex; i++)
                newInd.addState(indB.getState(i));
            for (int i = stateAIndex + 1; i < indA.getStateSize(); i++)
                newInd.addState(indA.getState(i));
        } else {
            for (int i = 0; i <= stateBIndex; i++)
                newInd.addState(indB.getState(i));
            for (int i = stateAIndex; i < indA.getStateSize(); i++)
                newInd.addState(indA.getState(i));
        }
        log.trace("Crossover parent A at {}: {}", stateAIndex, indA);
        log.trace("Crossover parent B at {}: {}", stateBIndex, indB);
        log.debug("Create {}", newInd);
        return newInd;
    }

}
