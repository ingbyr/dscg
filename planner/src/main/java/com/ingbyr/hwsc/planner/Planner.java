package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.QoS;
import com.ingbyr.hwsc.common.Service;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.yashp2.InnerPlannerYashp2;
import com.ingbyr.hwsc.planner.utils.UniformUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

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

    private Context context;

    private IndividualGenerator individualGenerator;

    private InnerPlanner innerPlanner;

    private Evaluator evaluator;

    private Crossover crossover;

    private Mutations mutations;

    private SurvivalSelector survivalSelector;

    private PlannerAnalyzer analyzer;

    private QoS preQoS;

    private BlockingQueue<Service> serviceToAddQueue;

    private BlockingQueue<Service> serviceToRemoveQueue;

    @Setter
    private StepMsgHandler stepMsgHandler;

    public void exec() {

        beforeExec();

        log.info("Create initial population");
        // Generate population
        List<Individual> population = new ArrayList<>(config.getPopulationSize());

        // Get max state size
        int candidateStartTimesSize = context.candidateStartTimes.length;
        int stateSize = Math.min(candidateStartTimesSize, config.getMaxStateSize());

        for (int i = 0; i < config.getPopulationSize(); i++) {
            // At least select 1
            int randomTimeSize = UniformUtils.rangeII(1, stateSize);
            population.add(individualGenerator.generate(randomTimeSize));
        }

        // Evaluate initial population
        evaluator.evaluate(population, innerPlanner);

        log.info("Start processing ...");
        // Start process
        int stopStepCount = 0;
        for (int gen = 0; gen < config.getMaxGen(); gen++) {

            StringBuilder stepMsg = new StringBuilder();
            stepMsg.append("Process (");
            stepMsg.append(gen);
            stepMsg.append("/");
            stepMsg.append(config.getMaxGen());
            stepMsg.append(")");

            log.info("{}", stepMsg.toString());
            stepMsgHandler.handle(stepMsg.toString());

            // Monitor service add or remove operation
            monitorServiceStatus(population);

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
            evaluator.evaluate(offspring, innerPlanner);

            // Survival selection
            population = survivalSelector.filter(population, offspring);

            // Check the termination condition
            QoS bestQoS = population.get(0).getQos();

            // Auto stop the process when no improvements
            if (config.isEnableAutoStop()) {
                log.debug("Current best {}", bestQoS);
                if (bestQoS.equals(preQoS)) {
                    if (++stopStepCount >= config.getAutoStopStep()) {
                        log.info("Auto stop process because of no improvements");
                        break;
                    }
                } else {
                    // Reset stop step count
                    stopStepCount = 0;
                }
                preQoS = bestQoS;
            } else {
                log.debug("Current best qos {}", bestQoS);
            }

            // Record best individual log
            analyzer.recordStepInfo(population);
        }

        log.info("Process is finished");
        afterExec();
    }

    private void monitorServiceStatus(List<Individual> population) {
        if (serviceToRemoveQueue != null && !serviceToRemoveQueue.isEmpty()) {
            List<Service> servicesToRemove = new ArrayList<>(serviceToRemoveQueue.size());
            while (!serviceToRemoveQueue.isEmpty()) {
                servicesToRemove.add(serviceToRemoveQueue.poll());
            }
            refreshPlannerContext();
            filterDeadIndividuals(population,servicesToRemove);
            supplementPopulation(population, config.getSurvivalSize());
        }

        if (serviceToAddQueue != null && !serviceToAddQueue.isEmpty()) {
            List<Service> servicesToAdd = new ArrayList<>(serviceToAddQueue.size());
            while (!serviceToAddQueue.isEmpty()) {
                servicesToAdd.add(serviceToAddQueue.poll());
            }
            refreshPlannerContext();
            replacePopulation(population, config.getSurvivalSize() >> 1);
        }
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

    protected void beforeExec() {
        analyzer.recordStartTime();

        // Check components
        if (stepMsgHandler == null) {
            stepMsgHandler = new StepMsgHandlerDefault();
            log.info("Not found step msg handler, so set to default");
        }
    }

    protected void afterExec() {
        log.info("================Analyzer================");
        analyzer.recordEndTime();
        analyzer.displayLogOnConsole();
    }

    private void checkPlannerConfig(PlannerConfig config) throws DAEXConfigException {
        log.info("Check the planner config {}", config);
        if (config.getPopulationSize() + config.getOffspringSize() < config.getSurvivalSize())
            throw new DAEXConfigException("populationSize + offspringSize < survivalSize");
    }

    public void setup(PlannerConfig plannerConfig, DataSetReader reader) throws DAEXConfigException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //Reset global id for individual
        Individual.globalId = 0;

        // If already setup then skip
        if (plannerConfig != null && plannerConfig.equals(this.config))
            return;
        // Else check new config
        checkPlannerConfig(plannerConfig);
        config = plannerConfig;

        dataSetReader = reader;
        dataSetReader.setDataset(config.getDataset());

        innerPlanner = new InnerPlannerYashp2(this.dataSetReader.getServiceMap(), this.dataSetReader.getConceptMap(), 1);

        evaluator = (Evaluator) Class.forName(PlannerConfig.EVALUATOR_CLASS_PREFIX + config.getEvaluator())
                .getDeclaredConstructor().newInstance();
        evaluator.setInnerPlannerMaxStep(config.getInnerPlanMaxStep());
        evaluator.setMaxStateSize(config.getMaxStateSize());

        context = new Context();
        context.setup(this.dataSetReader);

        individualGenerator = new IndividualGenerator(this.dataSetReader, context);

        crossover = new CrossoverSwapState();

        mutations = new Mutations();
        mutations.addMutation(
                new MutationAddState(context, config.getMutationAddStateRadius()),
                config.getMutationAddStateWeight());
        mutations.addMutation(
                new MutationAddConcept(context,
                        config.getMutationAddConceptChangePossibility(),
                        config.getMutationAddConceptAddPossibility()),
                config.getMutationAddConceptWeight());
        mutations.addMutation(new MutationDelState(), config.getMutationDelStateWeight());
        mutations.addMutation(new MutationDelConcept(), config.getMutationDelConceptWeight());

        Fitness fitness = (Fitness) Class.forName(PlannerConfig.FITNESS_CLASS_PREFIX + config.getFitness())
                .getDeclaredConstructor().newInstance();
        survivalSelector = new SurvivalSelector(config.getSurvivalSize(), fitness);

        analyzer = new PlannerAnalyzer();
        analyzer.setDataset(config.getDataset());
        analyzer.setSave2file(config.saveToFile);
    }

    public static void main(String[] args) throws ConfigurationException, DAEXConfigException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        PlannerConfig config = new PlannerConfigFile();
        log.debug("{}", config);
        Planner planner = new Planner();
        planner.setup(config, new XMLDataSetReader());
        planner.exec();
    }
}
