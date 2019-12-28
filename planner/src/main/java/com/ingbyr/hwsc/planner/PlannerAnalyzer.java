package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.QoS;
import com.ingbyr.hwsc.common.Service;
import com.ingbyr.hwsc.dataset.Dataset;
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

    // Best QoS
    @Getter
    private final List<QoS> bestQos = new LinkedList<>();

    // All QoS
    List<List<QoS>> allQoS = new LinkedList<>();

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

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public void recordStepInfo(List<Individual> pop) {
        allQoS.add(pop.stream().map(Individual::getQos).collect(Collectors.toList()));
        Individual bestInd = pop.get(0);
        QoS realQoS = bestInd.getQos();
        bestQos.add(realQoS);
        log.debug("Population :");
        for (Individual individual : pop) {
            log.debug("{}", individual.toSimpleInfo());
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
        log.info("Best QoS log:");
        System.out.println(bestQos);
        log.info("All QoS:");
        for (List<QoS> qos : allQoS) {
            System.out.println(qos);
        }
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
