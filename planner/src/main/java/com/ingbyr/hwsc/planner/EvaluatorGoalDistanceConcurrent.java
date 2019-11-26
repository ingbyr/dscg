package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.model.State;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Implementation to the DAEX: Algorithm 1 evaluate(Ind, hwsc)
 *
 * @author ingbyr
 */
@Slf4j
@Builder
public class EvaluatorGoalDistanceConcurrent implements Evaluator {

    private final ForkJoinPool commonPool = ForkJoinPool.commonPool();

    private int bMax;

    // TODO lMax not work
    private int lMax;

    /**
     * Use fork join framework to solve problem
     */
    @Builder
    public static class EvaluatorTask extends RecursiveAction {

        // TODO config
        private static final int THRESHOLD = 20;

        private int bMax;

        // TODO lMax not work
        private int lMax;

        private List<Individual> individuals;

        private int start;

        private int end;

        private InnerPlanner innerPlanner;

        @Override
        protected void compute() {
            if (end - start > THRESHOLD) {
                int mid = start + (end - start) / 2;
                EvaluatorTask evaluatorTask1 = EvaluatorTask.builder()
                        .bMax(bMax).lMax(lMax)
                        .individuals(individuals)
                        .start(start)
                        .end(mid)
                        .innerPlanner(innerPlanner.copy())
                        .build();
                EvaluatorTask evaluatorTask2 = EvaluatorTask.builder()
                        .bMax(bMax).lMax(lMax)
                        .individuals(individuals)
                        .start(mid)
                        .end(end)
                        .innerPlanner(innerPlanner.copy())
                        .build();
                invokeAll(evaluatorTask1, evaluatorTask2);
            } else {
                for (int i = start; i < end; i++) {
                    doEvaluation(individuals.get(i));
                }
            }
        }

        private boolean doEvaluation(Individual individual) {
            // Intermediate goal counter
            int k = 0;
            // Useful states counter
            int u = 0;
            // Total search steps
            int b = 0;
            Set<Concept> goalSet = individual.getGoalSet();
            List<Solution> solutions = new LinkedList<>();
            Set<Concept> middleInputSet = individual.getInputSet();
            Set<Concept> middleGoalSet = null;

            log.debug("Evaluate the {}", individual.getId());

            int reachedStateIndex;
            for (reachedStateIndex = 0; reachedStateIndex < individual.getStateSize(); reachedStateIndex++) {
                State currentState = individual.getState(reachedStateIndex);
                log.trace("Evaluate the state {}", currentState);
                middleGoalSet = currentState.concepts;

                Solution solution = innerPlanner.solve(middleInputSet, middleGoalSet, bMax);

                if (solution == null || solution.services == null) {
                    Solution noSolution = new Solution(
                            null,
                            10 * k * dist(middleInputSet, goalSet) + individual.getStateSize() - u);
                    individual.setServices(noSolution.services);
                    individual.fitness = noSolution.searchCost;
                    individual.lastReachedStateIndex = reachedStateIndex - 1;
                    individual.isFeasible = false;
                    return false;
                } else if (solution.services.size() > 0) {
                    u++;
                    b += solution.searchCost;
                    solutions.add(solution);
                }

                middleInputSet = execSolution(middleInputSet, solution);
                k++;
            }

            // Solution is feasible
            Solution solution = compressSolution(solutions);
            solution.searchCost = solution.searchCost
                    + (double) (individual.getStateSize() - u + 1) / solution.searchCost
                    + b / (double) (lMax * bMax);

            individual.lastReachedStateIndex = reachedStateIndex;
            individual.isFeasible = true;
            individual.setServices(solution.services);
            // Feasible individual's fitness may be recalculate (such as indicator qos).
            individual.fitness = solution.searchCost;

            return true;
        }


        /**
         * For any complete state i and partial state g, dist(i, g) is the number of
         * atoms in g that are not in i
         *
         * @param middleSet
         * @param middleGoalSet
         * @return
         */
        private int dist(Set<Concept> middleSet, Set<Concept> middleGoalSet) {
            int distance = 0;
            for (Concept goalConcept : middleGoalSet)
                if (!middleSet.contains(goalConcept))
                    distance++;
            return distance;
        }

        /**
         * Compress solutions to one solution
         *
         * @param solutions
         * @return
         */
        private static Solution compressSolution(List<Solution> solutions) {
            List<Service> serviceResult = new LinkedList<>();
            double searchCost = 0.0;
            for (Solution solution : solutions) {
                serviceResult.addAll(solution.services);
                searchCost += solution.searchCost;
            }
            return new Solution(serviceResult, searchCost);
        }


        /**
         * Execute a solution
         *
         * @param inputSet Input concept set
         * @param solution Solution that will be executed
         * @return Output concept set
         */
        private static Set<Concept> execSolution(Set<Concept> inputSet, Solution solution) {
            Set<Concept> outputSet = new HashSet<>(inputSet);
            solution.services.forEach(service -> outputSet.addAll(service.getOutputConceptSet()));
            return outputSet;
        }
    }

    @Override
    public void evaluate(List<Individual> individuals, InnerPlanner innerPlanner) {
        log.debug("Start evaluating");
        RecursiveAction action = EvaluatorTask.builder()
                .bMax(bMax).lMax(lMax)
                .individuals(individuals)
                .start(0)
                .end(individuals.size())
                .innerPlanner(innerPlanner.copy())
                .build();
        commonPool.execute(action);
        action.join();
        log.debug("Evaluation is finished");
    }
}
