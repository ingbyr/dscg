package com.ingbyr.hwsc.planner;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ingbyr.hwsc.common.BestQos;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.common.util.WorkDir;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.planner.exception.NotValidSolutionException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author ingbyr
 */
@Slf4j
public class PlannerAnalyzer {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // Fitness log
    private final List<Double> fitnessLog = new LinkedList<>();

    // Qos log
    @Getter
    private final List<Qos> realQosLog = new LinkedList<>();

    @Getter
    private List<Object> echartQosLog;

    @Getter
    private Instant startTime;

    @Getter
    private Instant endTime;

    @Getter
    private double runtime;

    @Getter
    private Dataset dataset;

    @Getter
    private BestQos bestQos;

    public PlannerAnalyzer() {
        echartQosLog = new ArrayList<>();
        String[] qosTypes = new String[Qos.NAMES.length + 1];
        System.arraycopy(Qos.NAMES, 0, qosTypes, 1, Qos.NAMES.length);
        qosTypes[0] = "Step";
        echartQosLog.add(qosTypes);
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
        this.bestQos = dataset.getBestQos();
    }

    public void addLog(Individual individual) {
        fitnessLog.add(individual.getFitness());

        Qos realQos = individual.getQos();
        log.debug("Fitness {}, Real {}", individual.getFitness(), realQos);
        realQosLog.add(realQos);

        double[] qosWithStep = new double[Qos.NAMES.length + 1];
        System.arraycopy(realQos.getValues(), 0, qosWithStep, 1, Qos.NAMES.length);
        qosWithStep[0] = realQosLog.size();
        echartQosLog.add(qosWithStep);
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
        displayLog();
    }

    private void displayLog() {
        log.info("Process log:");
        Iterator<Double> fitnessItr = fitnessLog.iterator();
        Iterator<Qos> qosLogItr = realQosLog.iterator();
        int step = 0;
        while (fitnessItr.hasNext() && qosLogItr.hasNext()) {
            log.debug("[{}] Fitness {}, Qos {}", step, fitnessItr.next(), qosLogItr.next());
            step++;
        }

        System.out.println(realQosLog);
    }

    public void saveQosLogToFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        File qosLogFile = WorkDir.LOG_DIR.resolve("qos@" + dataset.name() + "@"
                + LocalDateTime.now().format(DATETIME_FORMATTER)
                + ".json").toFile();
        log.info("Save qos log to {}", qosLogFile);
        writer.writeValue(qosLogFile, getEchartQosLog());
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
