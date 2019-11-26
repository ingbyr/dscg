package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.planner.indicators.BinaryIndicator;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerInnerPlannerYashp2;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO use fork/join to speed up
 * TODO process auto stop when no improvements
 * TODO support changeable services
 *
 * @author ingbyr
 */
@Slf4j
@Builder
@Setter
public class Planner {

    private DataSetReader dataSetReader;

    private PlannerConfig config;

    private ConceptTime conceptTime;

    private IndividualGenerator individualGenerator;

    private InnerPlanner innerPlanner;

    private Evaluator evaluator;

    private Crossover crossover;

    private int populationSize;

    private int offspringSize;

    private int survivalSize;

    private double pCross;

    // Not work in this version. pMut = 1 - pCross
    private double pMut;

    private Mutations mutations;

    private SurvivalSelector survivalSelector;

    private int maxGen;

    private boolean enableCrossover;

    private boolean enableMutate;

    private PlannerAnalyzer analyzer;

    private boolean enableConcurrent;

    private boolean enableAutoStop;

    private int stopStep;

    private Qos preQos;

    private int stopStepCount;

    public void exec() {

        beforeExec();

        log.info("Create initial population");
        // Generate population
        List<Individual> population = new ArrayList<>(populationSize);
        int candidateStartTimesSize = conceptTime.candidateStartTimes.length;
        for (int i = 0; i < populationSize; i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, candidateStartTimesSize);
            population.add(individualGenerator.generate(randomTimeSize));
        }
        // Evaluate initial population
        evaluator.evaluate(population, innerPlanner);

        log.info("Start processing ...");
        // Start process
        for (int gen = 0; gen < maxGen; gen++) {
            log.info("Progress ({}/{})", gen, maxGen);
            // Create offspring
            List<Individual> offspring = new ArrayList<>(offspringSize);

            // Create new individual
            for (int i = 0; i < offspringSize; i++) {
                Individual individual1 = UniformUtils.oneFromList(population);
                Individual newIndividual = null;
                // Crossover
                if (enableCrossover && UniformUtils.p() < pCross) {
                    Individual individual2 = UniformUtils.oneFromList(population);
                    newIndividual = crossover.doCrossover(individual1, individual2);

                    if (newIndividual == null) {
                        log.error("Crossover new individual is null");
                        return;
                    }

                    // New individual is same to the parents, mutate it
                    if (newIndividual.equals(individual1) || newIndividual.equals(individual2)) {
                        mutations.mutate(newIndividual);
                    }

                } else if (enableMutate) {
                    // If no crossover, then mutate it
                    newIndividual = individual1.copy();
                    mutations.mutate(newIndividual);

                    if (newIndividual == null) {
                        log.error("Mutation new individual is null");
                        return;
                    }
                }
                offspring.add(newIndividual);
            }

            // Evaluation
            evaluator.evaluate(offspring, innerPlanner);

            // Survival selection
            population = survivalSelector.filter(population, offspring);

            // Record best individual log
            analyzer.addLog(population.get(0));

            // Check the termination condition
            Qos bestQos = population.get(0).getQos();


            // Auto stop the process when no improvements
            if (enableAutoStop) {
                log.debug("Previous best qos {}", preQos);
                log.debug("Current best qos {}", bestQos);
                if (bestQos.equals(preQos)) {
                    if (++stopStepCount >= stopStep) {
                        log.info("Auto stop process because of no improvements");
                        break;
                    }
                } else {
                    // Reset stop step count
                    stopStepCount = 0;
                }
                preQos = bestQos;
            } else {
                log.debug("Current best qos {}", bestQos);
            }
        }

        log.info("Process is finished");
        afterExec();

    }

    protected void beforeExec() {
        try {
            checkConfig();
        } catch (DAEXConfigException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        analyzer.recordStartTime();
    }

    protected void afterExec() {
        analyzer.recordEndTime();
        analyzer.displayRuntime();
        analyzer.displayLog();
        try {
            analyzer.transToUIData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkConfig() throws DAEXConfigException {
        log.info("Check the planner config");
        log.info(config.toString());

        if (populationSize + offspringSize < survivalSize)
            throw new DAEXConfigException("populationSize + offspringSize < survivalSize");
    }

    public static void main(String[] args) throws ConfigurationException {
        PlannerConfig config = new PlannerLocalConfig();

        DataSetReader dataSetReader = new XMLDataSetReader(config.getDataset());
        dataSetReader.process();

        InnerPlanner innerPlanner = new InnerInnerPlannerYashp2(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        Evaluator evaluator = null;
        if (config.isEnableConcurrentMode())
            evaluator = EvaluatorGoalDistanceConcurrent.builder().bMax(10).lMax(10).build();
        else
            evaluator = EvaluatorGoalDistance.builder().bMax(10).lMax(10).build();

        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        IndividualGenerator individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        Crossover crossover = new CrossoverSwapState();

        // TODO Move config
        Mutations mutations = new Mutations();
        mutations.addMutation(new MutationAddState(conceptTime, 0), config.getMutationAddStateWeight());
        mutations.addMutation(new MutationAddConcept(conceptTime, 0.5, 0.5), config.getMutationAddConceptWeight());
        mutations.addMutation(new MutationDelState(), config.getMutationDelStateWeight());
        mutations.addMutation(new MutationDelConcept(), config.getMutationDelConceptWeight());

        SurvivalSelector survivalSelector = new SurvivalSelectorIndicator(config.getSurvivalSize(), new BinaryIndicator(2));

        PlannerAnalyzer analyzer = new PlannerAnalyzer();

        Planner planner = Planner.builder()
                .dataSetReader(dataSetReader)
                .config(config)
                .conceptTime(conceptTime)
                .individualGenerator(individualGenerator)
                .innerPlanner(innerPlanner)
                .crossover(crossover)
                .evaluator(evaluator)
                .populationSize(config.getPopulationSize())
                .offspringSize(config.getOffspringSize())
                .pCross(config.getCrossoverPossibility())
                .pMut(config.getMutationPossibility())
                .mutations(mutations)
                .survivalSelector(survivalSelector)
                .maxGen(config.getMaxGen())
                .enableMutate(true)
                .enableCrossover(true)
                .analyzer(analyzer)
                .enableConcurrent(config.isEnableConcurrentMode())
                .stopStep(config.getAutoStopStep())
                .enableAutoStop(config.isEnableAutoStop())
                .build();

        planner.exec();
    }
}
