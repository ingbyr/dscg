package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.planner.indicators.BinaryIndicator;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerPlannerYashp2;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO support changeable services
 *
 * @author ingbyr
 */
@Slf4j
@NoArgsConstructor
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
                log.debug("Current best {}", bestQos);
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

    private void checkAndSaveConfig(PlannerConfig config) throws DAEXConfigException {
        log.info("Check the planner config {}", config);
        if (populationSize + offspringSize < survivalSize)
            throw new DAEXConfigException("populationSize + offspringSize < survivalSize");
        this.config = config;
    }

    public void setup(PlannerConfig config) throws DAEXConfigException {
        // If already setup then skip
        if (config != null && config.equals(this.config))
            return;

        checkAndSaveConfig(config);

        populationSize = config.getPopulationSize();
        offspringSize = config.getOffspringSize();
        pCross = config.getCrossoverPossibility();
        pMut = config.getMutationPossibility();
        maxGen = config.getMaxGen();
        enableMutate = true;
        enableCrossover = true;
        enableConcurrent = config.isEnableConcurrentMode();
        stopStep = config.getAutoStopStep();
        enableAutoStop = config.isEnableAutoStop();

        dataSetReader = new XMLDataSetReader(config.getDataset());
        dataSetReader.process();

        innerPlanner = new InnerPlannerYashp2(dataSetReader.getServiceMap(), dataSetReader.getConceptMap(), 1);

        if (enableConcurrent)
            evaluator = EvaluatorGoalDistanceConcurrent.builder().bMax(10).lMax(10).build();
        else
            evaluator = EvaluatorGoalDistance.builder().bMax(10).lMax(10).build();

        conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);

        individualGenerator = new IndividualGenerator(dataSetReader, conceptTime);

        crossover = new CrossoverSwapState();

        // TODO Move config
        mutations = new Mutations();
        mutations.addMutation(new MutationAddState(conceptTime, 0), config.getMutationAddStateWeight());
        mutations.addMutation(new MutationAddConcept(conceptTime, 0.5, 0.5), config.getMutationAddConceptWeight());
        mutations.addMutation(new MutationDelState(), config.getMutationDelStateWeight());
        mutations.addMutation(new MutationDelConcept(), config.getMutationDelConceptWeight());

        survivalSelector = new SurvivalSelectorIndicator(config.getSurvivalSize(), new BinaryIndicator(2));

        analyzer = new PlannerAnalyzer();
    }

    public static void main(String[] args) throws ConfigurationException, DAEXConfigException {
        PlannerConfig config = new PlannerLocalConfig();
        log.debug("{}", config);
        Planner planner = new Planner();
        planner.setup(config);
        planner.exec();
    }
}
