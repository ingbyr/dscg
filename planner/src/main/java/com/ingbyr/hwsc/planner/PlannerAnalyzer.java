package com.ingbyr.hwsc.planner;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dataset.util.QosUtils;
import com.ingbyr.hwsc.planner.exception.NotValidSolutionException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author ingbyr
 */
@Slf4j
public class PlannerAnalyzer {

    // Fitness log
    private final List<Double> fitnessLog = new LinkedList<>();

    // Qos log
    private final List<Qos> realQosLog = new LinkedList<>();

    private Instant startTime;

    private Instant endTime;

    public void addLog(Individual individual) {
        fitnessLog.add(individual.getFitness());
        realQosLog.add(individual.getQos());
    }

    public void displayLog() {
        log.info("Process log:");
        Iterator<Double> fitnessItr = fitnessLog.iterator();
        Iterator<Qos> originQosItr = realQosLog.iterator();
        int step = 0;
        while (fitnessItr.hasNext() && originQosItr.hasNext()) {
            log.debug("[{}] Fitness {}", step, fitnessItr.next());
            log.debug("[{}] origin qos {}", step, originQosItr.next());
            log.debug("");
            step++;
        }
    }

    public void recordStartTime() {
        startTime = Instant.now();
    }

    public void recordEndTime() {
        endTime = Instant.now();
    }

    public void displayRuntime() {
        log.info("Time used {} seconds", Duration.between(startTime, endTime).toMillis() / 1000.0F);
    }

    /**
     * TODO Move this to web app
     *
     * @throws IOException
     */
    public void transToUIData() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        List<Object> data = new ArrayList<>(realQosLog.size() + 1);
        String[] qosTypes = new String[Qos.NAMES.length + 1];
        System.arraycopy(Qos.NAMES, 0, qosTypes, 1, Qos.NAMES.length);
        qosTypes[0] = "Step";
        data.add(qosTypes);

        int qosNum = Qos.NAMES.length;
        // Add step to qos log
        for (int i = 0; i < realQosLog.size(); i++) {
            double[] qosWithStep = new double[qosNum + 1];
            System.arraycopy(realQosLog.get(i).getValues(), 0, qosWithStep, 1, qosNum);
            qosWithStep[0] = i;
            data.add(qosWithStep);
        }

        writer.writeValue(new File(".//log//qos.json"), data);
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
