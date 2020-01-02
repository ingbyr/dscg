package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.MemoryUtils;
import com.ingbyr.hwsc.common.Qos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
public class PlannerAnalyzer {

    // Log
    List<List<Qos>> qosLog = new LinkedList<>();
    List<List<Qos>> rawQosLog = new LinkedList<>();
    List<Qos> bestQosLog = new LinkedList<>();
    List<Double> GDLog = new LinkedList<>();
    List<Double> IGDLog = new LinkedList<>();
    List<Long> memoryLog = new LinkedList<>();

    @Getter
    private PlannerResult result = new PlannerResult();

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
    private PlannerIndicator indicator;

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
        qosLog.add(pop.stream().map(Individual::getQos).collect(Collectors.toList()));
        rawQosLog.add(pop.stream().map(Individual::getQos).collect(Collectors.toList()));

        log.debug("Population :");
        for (Individual individual : pop) {
            log.debug("{}", individual.toSimpleInfo());
        }

        memoryLog.add(MemoryUtils.currentUsedMemory());

        if (fitness instanceof FitnessParetoFront) {
            double stepGD = indicator.GD(pop);
            log.debug("GD: {}", stepGD);
            GDLog.add(stepGD);
            // TODO disable in bench
//            double stepIGD = indicator.IGD(pop);
//            log.debug("IGD: {}", stepIGD);
//            IGDLog.add(stepIGD);

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
        result.indNum = Individual.globalId;
        result.runtime = runtime;
        result.memoryLog = memoryLog;
    }

    public void displayLogOnConsole() {
        log.info("Time used {} seconds", getRuntime());
        log.debug("Last population:");
        for (Individual ind : lastPop) {
            log.debug("{}", ind.toSimpleInfo());
        }

        if (fitness instanceof FitnessParetoFront) {
            log.info("GD: {}", GDLog);
            log.info("IGD: {}", IGDLog);
        } else {
            log.info("Best qos log: {}", bestQosLog);
        }
    }

    public void setGen(int gen) {
        result.gen = gen;
    }
}
