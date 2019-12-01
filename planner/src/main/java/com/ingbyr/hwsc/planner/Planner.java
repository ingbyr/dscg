package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.planner.indicators.BinaryIndicator;
import com.ingbyr.hwsc.planner.indicators.SumIndicator;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerPlannerYashp2;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.Getter;
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
@Getter
public class Planner {

    private DataSetReader dataSetReader;

    private PlannerConfig config;

    private ConceptTime conceptTime;

    private IndividualGenerator individualGenerator;

    private InnerPlanner innerPlanner;

    private Evaluator evaluator;

    private Crossover crossover;

    private Mutations mutations;

    private SurvivalSelector survivalSelector;

    private PlannerAnalyzer analyzer;

    private Qos preQos;

    public void exec() {

        beforeExec();

        log.info("Create initial population");
        // Generate population
        List<Individual> population = new ArrayList<>(config.getPopulationSize());
        int candidateStartTimesSize = conceptTime.candidateStartTimes.length;
        for (int i = 0; i < config.getPopulationSize(); i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, candidateStartTimesSize);
            population.add(individualGenerator.generate(randomTimeSize));
        }
        // Evaluate initial population
        evaluator.evaluate(population, innerPlanner);

        log.info("Start processing ...");
        // Start process
        int stopStepCount = 0;
        for (int gen = 0; gen < config.getMaxGen(); gen++) {
            log.info("Progress ({}/{})", gen, config.getMaxGen());
            // Create offspring
            List<Individual> offspring = new ArrayList<>(config.getOffspringSize());

            // Create new individual
            for (int i = 0; i < config.getOffspringSize(); i++) {
                Individual individual = UniformUtils.oneFromList(population);
                Individual newIndividual = null;
                // Crossover
                if (UniformUtils.p() < config.getCrossoverPossibility()) {
                    Individual individual2 = UniformUtils.oneFromList(population);
                    newIndividual = crossover.doCrossover(individual, individual2);

                    if (newIndividual == null) {
                        log.error("Crossover new individual is null");
                        return;
                    }

                    // New individual is same to the parents, mutate it
                    while (newIndividual.equals(individual) || newIndividual.equals(individual2)) {
                        mutations.mutate(newIndividual);
                    }

                } else {
                    // If no crossover, then mutate it
                    newIndividual = individual.copy();

                    // Must do once mutation
                    while (individual.equals(newIndividual)) {
                        mutations.mutate(newIndividual);
                    }

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

            // Check the termination condition
            Qos bestQos = population.get(0).getQos();

            // Auto stop the process when no improvements
            if (config.isEnableAutoStop()) {
                log.debug("Current best {}", bestQos);
                if (bestQos.equals(preQos)) {
                    if (++stopStepCount >= config.getAutoStopStep()) {
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

            // Record best individual log
            analyzer.addLog(population.get(0));
        }

        log.info("Process is finished");
        afterExec();

    }

    protected void beforeExec() {
        analyzer.recordStartTime();
    }

    protected void afterExec() {
        analyzer.recordEndTime();
        analyzer.buildEchartData();
        analyzer.displayLogOnConsole();

        if (config.saveToFile) {
            try {
                analyzer.saveQosLogToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkConfig(PlannerConfig config) throws DAEXConfigException {
        log.info("Check the planner config {}", config);
        if (config.getPopulationSize() + config.getOffspringSize() < config.getSurvivalSize())
            throw new DAEXConfigException("populationSize + offspringSize < survivalSize");
    }

    public void setup(PlannerConfig plannerConfig, DataSetReader reader) throws DAEXConfigException {
        // If already setup then skip
        if (plannerConfig != null && plannerConfig.equals(this.config))
            return;

        checkConfig(plannerConfig);
        config = plannerConfig;
        dataSetReader = reader;
        dataSetReader.setDataset(config.getDataset());

        innerPlanner = new InnerPlannerYashp2(this.dataSetReader.getServiceMap(), this.dataSetReader.getConceptMap(), 1);

        if (config.isEnableConcurrentMode())
            evaluator = EvaluatorGoalDistanceConcurrent.builder().bMax(10).lMax(10).build();
        else
            evaluator = EvaluatorGoalDistance.builder().bMax(10).lMax(10).build();

        conceptTime = new ConceptTime();
        conceptTime.build(this.dataSetReader);

        individualGenerator = new IndividualGenerator(this.dataSetReader, conceptTime);

        crossover = new CrossoverSwapState();

        // TODO Move config
        mutations = new Mutations();
        mutations.addMutation(new MutationAddState(conceptTime, 0), config.getMutationAddStateWeight());
        mutations.addMutation(new MutationAddConcept(conceptTime, 0.5, 0.5), config.getMutationAddConceptWeight());
        mutations.addMutation(new MutationDelState(), config.getMutationDelStateWeight());
        mutations.addMutation(new MutationDelConcept(), config.getMutationDelConceptWeight());

        survivalSelector = new SurvivalSelectorIndicator(config.getSurvivalSize(), new BinaryIndicator(1));
//        survivalSelector = new SurvivalSelectorIndicator(config.getSurvivalSize(), new SumIndicator());

        analyzer = new PlannerAnalyzer();
        analyzer.setDataset(config.getDataset());
    }

    public static void main(String[] args) throws ConfigurationException, DAEXConfigException {
        PlannerConfig config = new PlannerLocalConfig();
        log.debug("{}", config);
        Planner planner = new Planner();
        planner.setup(config, new XMLDataSetReader());
        planner.exec();
    }
}
