package com.ingbyr.dscg;

import com.ingbyr.dscg.exception.HWSCConfigException;
import com.ingbyr.dscg.planner.Planner;
import com.ingbyr.dscg.utils.UniformUtils;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * TODO support changeable services
 *
 * @author ingbyr
 */
@Slf4j
@NoArgsConstructor
@Getter
public class Dscg {

    private DataSetReader dataSetReader;

    private DscgConfig config;

    private HeuristicInfo h;

    private IndividualGenerator individualGenerator;

    private Planner planner;

    private Evaluate evaluate;

    private Crossover crossover;

    private Mutations mutations;

    private Fitness fitness;

    private SurvivalSelector survivalSelector;

    private DscgAnalyzer analyzer;

    private double popIndicator;

    private DscgIndicator dscgIndicator;

    private BlockingQueue<Service> serviceToAddQueue;

    private BlockingQueue<Service> serviceToRemoveQueue;

    @Setter
    private StepMsgHandler stepMsgHandler;

    public void exec() {
        resetAnalyzer();

        analyzer.recordStartTime();
        // Generate population
        List<Individual> population = new ArrayList<>(config.getPopulationSize());

        // Get max state size
        int candidateStartTimesSize = h.candidateStartTimes.length;
        int stateSize = Math.min(candidateStartTimesSize, config.getMaxStateSize());

        for (int i = 0; i < config.getPopulationSize(); i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, stateSize);
            population.add(individualGenerator.generate(randomTimeSize));
        }

        // Evaluate initial population
        evaluate.evaluate(population, planner);

        log.info("Start processing ...");
        // Start process
        int stopStepCount = 0;
        int gen = 0;
        for (; gen < config.getMaxGen(); gen++) {

            // Build step message
            StringBuilder stepMsg = new StringBuilder();
            stepMsg.append("Process (");
            stepMsg.append(gen);
            stepMsg.append("/");
            stepMsg.append(config.getMaxGen());
            stepMsg.append(")");

            log.info("{}", stepMsg.toString());

//             Step message callback function
            if (stepMsgHandler != null)
                stepMsgHandler.handle(stepMsg.toString());

//            // Monitor service add or remove operation
//            monitorServiceStatus(population);

            // Create offspring
            List<Individual> offspring = new ArrayList<>(config.getOffspringSize());

            // Create new individual
            for (int i = 0; i < config.getOffspringSize(); i++) {
                Individual individual = UniformUtils.oneFromList(population);
                Individual newIndividual;
                if (UniformUtils.p() < config.getCrossoverPossibility()) { // Crossover
                    newIndividual = doCrossover(individual, UniformUtils.oneFromList(population));
                } else { // Mutation
                    newIndividual = doMutation(individual);
                }
                offspring.add(newIndividual);
            }

            // Evaluation
            evaluate.evaluate(offspring, planner);

            // Survival selection
            population = survivalSelector.filter(population, offspring);

            // Analyze current step
            double currentPopIndicator = analyzer.recordStepInfo(population);

            // Check the termination condition
            if (config.isEnableAutoStop()) {
                if (currentPopIndicator == popIndicator) {
                    if (++stopStepCount >= config.getAutoStopStep()) {
                        log.info("Auto stop process because of no improvements");
                        break;
                    }
                } else {
                    stopStepCount = 0;
                }
                popIndicator = currentPopIndicator;
            }
        }

        log.info("Process is finished");

        analyzer.recordEndTime();
        analyzer.setLastPop(population);
        analyzer.setGen(gen);
        analyzer.displayLogOnConsole();
    }

    private void monitorServiceStatus(List<Individual> population) {
//        if (serviceToRemoveQueue != null && !serviceToRemoveQueue.isEmpty()) {
//            List<Service> servicesToRemove = new ArrayList<>(serviceToRemoveQueue.size());
//            while (!serviceToRemoveQueue.isEmpty()) {
//                servicesToRemove.add(serviceToRemoveQueue.poll());
//            }
//            refreshPlannerContext();
//            filterDeadIndividuals(population,servicesToRemove);
//            supplementPopulation(population, config.getSurvivalSize());
//        }
//
//        if (serviceToAddQueue != null && !serviceToAddQueue.isEmpty()) {
//            List<Service> servicesToAdd = new ArrayList<>(serviceToAddQueue.size());
//            while (!serviceToAddQueue.isEmpty()) {
//                servicesToAdd.add(serviceToAddQueue.poll());
//            }
//            refreshPlannerContext();
//            replacePopulation(population, config.getSurvivalSize() >> 1);
//        }
    }

    private void replacePopulation(List<Individual> population, int replaceSize) {
    }

    private void refreshPlannerContext() {

    }

    private void supplementPopulation(List<Individual> population, int survivalSize) {

    }

    private void filterDeadIndividuals(List<Individual> population, List<Service> servicesToRemove) {

    }

    private Individual doCrossover(Individual individual1, Individual individual2) {

        Individual newIndividual = crossover.doCrossover(individual1, individual2);

        if (newIndividual == null) {
            log.error("Crossover new individual is null");
            return null;
        }

        // New individual is same to the parents, mutate it
        while (newIndividual.equals(individual1) || newIndividual.equals(individual2)) {
            mutations.mutate(newIndividual);
        }
        return newIndividual;
    }

    private Individual doMutation(Individual individual) {
        Individual newIndividual = individual.copy();
        // Must do once mutation
        while (individual.equals(newIndividual)) {
            mutations.mutate(newIndividual);
        }
        return newIndividual;
    }

    private void checkPlannerConfig(DscgConfig config) throws HWSCConfigException {
        log.info("Check the planner config {}", config);
        if (config.getPopulationSize() + config.getOffspringSize() < config.getSurvivalSize())
            throw new HWSCConfigException("populationSize + offspringSize < survivalSize");
    }

    private void resetAnalyzer() {
        analyzer = new DscgAnalyzer();
        analyzer.setDataset(config.getDataset());
        analyzer.setFitness(fitness);
        analyzer.setIndicator(dscgIndicator);
        Individual.globalId = 0;
    }

    public void setup(DscgConfig dscgConfig, DataSetReader reader) throws HWSCConfigException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        //Reset global id for individual
        Individual.globalId = 0;

        // Reset GD
        this.popIndicator = Double.MAX_VALUE;

        // If already setup then skip
        if (dscgConfig != null && dscgConfig.equals(this.config))
            return;
        // Else check new config
        checkPlannerConfig(dscgConfig);
        config = dscgConfig;

        dataSetReader = reader;
        dataSetReader.setDataset(config.getDataset());

        evaluate = (Evaluate) Class.forName(DscgConfig.EVALUATOR_CLASS_PREFIX + config.getEvaluator())
                .getDeclaredConstructor().newInstance();
        evaluate.setPlannerMaxStep(config.getPlanMaxStep());
        evaluate.setMaxStateSize(config.getMaxStateSize());

        h = new HeuristicInfo();
        h.setup(this.dataSetReader);

        planner = (Planner) Class.forName(DscgConfig.PLANNER_CLASS_PREFIX + config.getPlanner())
                .getDeclaredConstructor().newInstance();
        planner.setConceptMap(h.getConceptMap());
        planner.setServiceMap(h.getServiceMap());

        individualGenerator = new IndividualGenerator(this.dataSetReader, h);

        crossover = new CrossoverSwapState();

        mutations = new Mutations();
        mutations.addMutation(
                new MutationAddState(h, config.getMutationAddStateRadius()),
                config.getMutationAddStateWeight());
        mutations.addMutation(
                new MutationAddConcept(h,
                        config.getMutationAddConceptChangePossibility(),
                        config.getMutationAddConceptAddPossibility()),
                config.getMutationAddConceptWeight());
        mutations.addMutation(new MutationDelState(), config.getMutationDelStateWeight());
        mutations.addMutation(new MutationDelConcept(), config.getMutationDelConceptWeight());

        fitness = (Fitness) Class.forName(DscgConfig.FITNESS_CLASS_PREFIX + config.getFitness())
                .getDeclaredConstructor().newInstance();
        survivalSelector = new SurvivalSelector(config.getSurvivalSize(), fitness);

        if (stepMsgHandler == null) {
            log.info("Not found step msg handler");
        }

        dscgIndicator = new DscgIndicator(config.getDataset());
    }
}
