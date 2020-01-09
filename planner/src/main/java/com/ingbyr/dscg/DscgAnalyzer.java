package com.ingbyr.dscg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.MemoryUtils;
import com.ingbyr.hwsc.common.Qos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ingbyr
 */
@NoArgsConstructor
@Slf4j
public class DscgAnalyzer {

    // Log
    List<Qos> bestQosLog = new LinkedList<>();
    List<Long> memoryLog = new LinkedList<>();
    List<List<Qos>> rawQosLog = new LinkedList<>();
    private PlannerResult result = new PlannerResult();

    @Getter
    private BenchStepResult benchResult = new BenchStepResult();

    @Getter
    @Setter
    private static class PlannerResult {
        List<List<double[]>> qosLog = new LinkedList<>();
        List<Double> gdLog = new LinkedList<>();
        List<Double> igdLog = new LinkedList<>();
    }

    @Getter
    private Instant startTime;

    @Getter
    private Instant endTime;

    @Getter
    private double runtime;

    @Getter
    @Setter
    private Dataset dataset;

    @Setter
    private DscgIndicator indicator;

    @Setter
    private Fitness fitness;

    @Setter
    @Getter
    private List<Individual> lastPop;


    /**
     * Record every step pop info and return GD as indicator of pop
     *
     * @param pop Population
     * @return The GD of pop
     */
    public Double recordStepInfo(List<Individual> pop) {
        log.debug("Best {}", pop.get(0).getQos());
        log.debug("Services {}", pop.get(0).getServices());
        result.qosLog.add(pop.stream().map(Individual::getQoSValues).collect(Collectors.toList()));
        rawQosLog.add(pop.stream().map(Individual::getQos).collect(Collectors.toList()));

//        log.debug("Population :");
//        for (Individual individual : pop) {
//            log.debug("{}", individual.toSimpleInfo());
//        }

        memoryLog.add(MemoryUtils.currentUsedMemory());

        if (fitness instanceof FitnessParetoFront) {
            double stepGD = indicator.GD(pop);
            log.debug("GD: {}", stepGD);
            result.gdLog.add(stepGD);

            // Disable IGD in bench mode
            double stepIGD = indicator.IGD(pop);
            log.debug("IGD: {}", stepIGD);
            result.igdLog.add(stepIGD);

            return stepGD;
        } else {
            bestQosLog.add(pop.get(0).getQos());
            return (double) pop.get(0).getId();
        }
    }

    void recordStartTime() {
        startTime = Instant.now();
    }

    void recordEndTime() {
        endTime = Instant.now();
        runtime = Duration.between(startTime, endTime).toMillis() / 1000.0;
        benchResult.indNum = Individual.globalId;
        benchResult.runtime = runtime;
        benchResult.memoryLog = memoryLog;
    }

    public void displayLogOnConsole() {

        log.info("Time used {} seconds", getRuntime());

        // TODO Save pop etc log to file
        // try {
        //     Path logFile = WorkDir.PLANNER_LOG_DIR.resolve(dataset.name() + ".json");
        //     log.info("Save result to {}", logFile);
        //     saveLogToFile(logFile);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    private void saveLogToFile(Path file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file.toFile(), result);
    }

    public void setGen(int gen) {
        benchResult.gen = gen;
    }
}
