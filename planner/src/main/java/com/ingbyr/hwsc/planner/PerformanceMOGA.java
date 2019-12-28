package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.QoS;
import com.ingbyr.hwsc.common.WorkDir;
import com.ingbyr.hwsc.dataset.Dataset;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class PerformanceMOGA {

    @Setter
    @Getter
    private Dataset dataset;

    public void GD(List<Individual> pop) throws IOException {
        // Load pareto front from local file
        String pfDataFilename = dataset.name() + "_" + String.join("_", QoS.NAMES) + ".txt";
        log.debug("load pareto front data from {}", pfDataFilename);
        List<String> pfData = Files.readAllLines(WorkDir.QOS_PF_DIR.resolve(pfDataFilename));
        List<QoS> pf = pfData.stream().map(data -> {
            double[] pfQoS = Arrays.stream(data.split(" ")).mapToDouble(Double::valueOf).toArray();
            return new QoS(pfQoS);
        }).collect(Collectors.toList());

        // Calculate GD

    }

    public static void main(String[] args) throws IOException {
        PerformanceMOGA p = new PerformanceMOGA();
        p.setDataset(Dataset.wsc2009_01);
        p.GD(null);
    }
}
