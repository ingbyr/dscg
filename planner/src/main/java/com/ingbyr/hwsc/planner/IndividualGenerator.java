package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ingbyr
 */
@Slf4j
@Setter
public final class IndividualGenerator {

    private Context context;

    private State inputState;

    private State goalState;

    public IndividualGenerator(DataSetReader dataSetReader, Context context) {
        this.context = context;
        this.inputState = new State(dataSetReader.getInputSet(), 0);
        this.goalState = new State(dataSetReader.getGoalSet(), context.time + 1);
        log.debug("Input state: {}", inputState);
        log.debug("Goal state: {}", goalState);
    }

    public Individual generate(int timeSize) {
        if (timeSize <= 0) {
            Individual noMiddleStateInd = new Individual();
            noMiddleStateInd.addState(inputState);
            noMiddleStateInd.addState(goalState);
            log.debug("Create {}", noMiddleStateInd);
            return noMiddleStateInd;
        }

        // Ordered list of timestamps
        int[] timeIndexes = UniformUtils.indexArray(context.candidateStartTimes.length, timeSize);
        Arrays.sort(timeIndexes);

        Individual individual = new Individual();
        // Add input set
        individual.addState(inputState);
        // Add middle goal set
        for (int timeIndex : timeIndexes) {
            expandIndividual(individual, context.candidateStartTimes[timeIndex]);
        }
        // Add goal set
        individual.addState(goalState);
        log.debug("Create {}", individual);
        return individual;
    }

    private void expandIndividual(Individual individual, int time) {
        Set<Concept> currentConcepts = context.conceptsAtTime.get(time);
        int selectedConceptSize = UniformUtils.rangeII(1, currentConcepts.size());
        Set<Concept> selectedConcepts = UniformUtils.set(currentConcepts, selectedConceptSize);
        individual.addState(new State(new HashSet<>(selectedConcepts), time));
    }
}
