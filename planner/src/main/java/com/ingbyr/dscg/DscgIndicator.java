package com.ingbyr.dscg;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.Qos;
import com.ingbyr.hwsc.common.WorkDir;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class DscgIndicator {

    @Getter
    private Dataset dataset;

    private List<Qos> pf;

    public DscgIndicator(Dataset dataset) throws IOException {
        this.dataset = dataset;
        Path pfFilePath = WorkDir.getParetoFrontFile(dataset.name());
        if (Files.exists(pfFilePath)) {
            log.info("Load pareto front data from {}", pfFilePath);
            this.pf = Files.readAllLines(pfFilePath).stream().map(Qos::ofNumpyFormat).collect(Collectors.toList());
        } else {
            log.info("No pareto front data, so add one zero vector");
            this.pf = new LinkedList<>();
            this.pf.add(new Qos(0.0));
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
        double min = pop.get(0).getFitness();
        return pop.stream().filter(ind -> ind.getFitness() == min).collect(Collectors.toList());
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
