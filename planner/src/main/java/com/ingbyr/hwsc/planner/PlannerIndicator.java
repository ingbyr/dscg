package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.Qos;
import com.ingbyr.hwsc.common.WorkDir;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class PlannerIndicator {

    @Getter
    private Dataset dataset;

    private List<Qos> pf;

    public PlannerIndicator(Dataset dataset) {
        this.dataset = dataset;
        Path pfFilePath = WorkDir.getQosParetoFrontFile(dataset.name());
        log.debug("Load pareto front data from {}", pfFilePath);
        try {
            this.pf = Files.readAllLines(pfFilePath).stream().map(Qos::ofNumpyFormat).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return dist^2 between x and y
     *
     * @param x Point x
     * @param y Point y
     * @return Dist^2
     */
    private static double dist2(double[] x, double[] y) {
        double d = 0.0;
        for (int i = 0; i < x.length; i++) {
            d += (x[i] - y[i]) * (x[i] - y[i]);
        }
        return d;
    }

    /**
     * Just use pareto front individual
     *
     * @param pop Population
     * @return Pareto front population
     */
    private static List<Individual> popParetoFront(List<Individual> pop) {
        return pop;
//        return pop.stream().filter(ind -> ind.getFitness() == 0.0).collect(Collectors.toList());
    }

    /**
     * Generational distance
     *
     * @param pop Population
     * @return GD
     */
    public double GD(List<Individual> pop) {
        List<Individual> popPF = popParetoFront(pop);
        double gd = 0.0;
        for (Individual ind : popPF) {
            Qos qos = ind.getQos();
            double m = Double.MAX_VALUE;
            for (Qos qosPF : pf) {
                m = Math.min(m, dist2(qos.getData(), qosPF.getData()));
            }
            gd += m;
        }
        return Math.sqrt(gd) / popPF.size();
    }

    /**
     * Inverted generational distance
     *
     * @param pop Population
     * @return IGD
     */
    public double IGD(List<Individual> pop) {
        List<Individual> popPF = popParetoFront(pop);
        double igd = 0.0;
        for (Qos qosPF : pf) {
            double m = Double.MAX_VALUE;
            for (Individual ind : popPF) {
                m = Math.min(m, dist2(qosPF.getData(), ind.getQos().getData()));
            }
            igd += m;
        }
        return igd / pf.size();
    }
}
