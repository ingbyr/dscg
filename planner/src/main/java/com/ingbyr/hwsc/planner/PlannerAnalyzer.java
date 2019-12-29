package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.Qos;
import com.ingbyr.hwsc.common.Service;
import com.ingbyr.hwsc.planner.exception.NotValidSolutionException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ingbyr
 */
@NoArgsConstructor
@Slf4j
public class PlannerAnalyzer {

    // Log
    List<List<Qos>> QosLog = new LinkedList<>();
    List<Double> GDLog = new LinkedList<>();
    List<Double> IGDLog = new LinkedList<>();

    @Getter
    private Instant startTime;

    @Getter
    private Instant endTime;

    @Getter
    private double runtime;

    @Getter
    private Dataset dataset;

    @Setter
    @Getter
    private boolean save2file;

    private PlannerIndicator indicator;

    @Setter
    private Fitness fitness;

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
        this.indicator = new PlannerIndicator(dataset);
    }

    /**
     * Record every step pop info and return GD as indicator of pop
     *
     * @param pop Population
     * @return The GD of pop
     */
    public String recordStepInfo(List<Individual> pop) {
        QosLog.add(pop.stream().map(Individual::getQos).collect(Collectors.toList()));

        log.debug("Population :");
        for (Individual individual : pop) {
            log.debug("{}", individual.toSimpleInfo());
        }

        double stepGD = indicator.GD(pop);
        log.info("GD: {}", stepGD);
        GDLog.add(stepGD);

        double stepIGD = indicator.IGD(pop);
        log.info("IGD: {}", stepIGD);
        IGDLog.add(stepIGD);

        if (fitness instanceof FitnessParetoFront) {
            return String.valueOf(stepGD);
        } else {
            return pop.get(0).getQos().toString();
        }
    }

    void recordStartTime() {
        startTime = Instant.now();
    }

    void recordEndTime() {
        endTime = Instant.now();
        runtime = Duration.between(startTime, endTime).toMillis() / 1000.0;
    }

    public void displayLogOnConsole() {
        log.info("Time used {} seconds", getRuntime());

        log.info("GD (Generational distance): ");
        System.out.println(GDLog);

        log.info("IGD (Inverted Generational Distance): ");
        System.out.println(IGDLog);
    }

    public static void displayPopulation(List<Individual> individuals) {
        log.debug("Current population:");
        individuals.forEach(individual -> {
            Integer id = individual.getId();
            log.debug("[{}] Individual {}", id, individual);
            log.debug("[{}] Services {}", id, individual.getServices());
            log.debug("[{}] Fitness {}, {}", id, individual.getFitness(), individual.getQos());
            log.debug("");
        });
    }

    public static void checkSolution(Set<Concept> input, Set<Concept> goal, List<Service> services) throws NotValidSolutionException {
        if (services == null) {
            log.error("Service list is null");
            return;
        }

        Set<Concept> concepts = new HashSet<>(input);
        for (Service service : services) {
            if (!concepts.containsAll(service.getInputConceptSet()))
                throw new NotValidSolutionException("Service " + service + " can not proceed because that some input concepts not existed");
            concepts.addAll(service.getOutputConceptSet());
        }

        if (!concepts.containsAll(goal))
            throw new NotValidSolutionException("Some goals are not contained when finishing execution");

        log.debug("The solution is valid");
    }

}
