package com.hwsc;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.NumpyDataFormat;
import com.ingbyr.hwsc.common.WorkDir;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class ParetoFront {

    // Forbid creating instance
    private ParetoFront() {
    }

    /**
     * Fake qos with pareto level
     */
    @ToString
    @EqualsAndHashCode
    private static final class QosLevel implements Comparable<QosLevel>, NumpyDataFormat {

        @EqualsAndHashCode.Exclude
        int l = -1;

        double[] v;

        public QosLevel(String rawQosData) {
            v = Arrays.stream(rawQosData.split(" ")).mapToDouble(Double::parseDouble).toArray();
        }

        @Override
        public int compareTo(QosLevel o) {
            return Integer.compare(l, o.l);
        }

        @Override
        public String toNumpy() {
            StringBuilder sb = new StringBuilder();
            for (double data : v) {
                sb.append(data);
                sb.append(' ');
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }

    private static boolean domain(QosLevel q1, QosLevel q2) {
        boolean hasBetter = false;
        int len = q1.v.length;
        for (int i = 0; i < len; i++) {
            if (q1.v[i] - q2.v[i] > 0)
                return false;
            else if (q1.v[i] - q2.v[i] < 0)
                hasBetter = true;
        }
        return hasBetter;
    }

    public static void find(Dataset dataset, String type) throws IOException {
        Set<QosLevel> dataSet = new HashSet<>();
        Path dataPath = WorkDir.getSearchSpaceFile(dataset.name(), type);
        log.info("Finding pareto front in {}", dataPath.getFileName());
        try (Stream<String> fs = Files.lines(dataPath)) {
            fs.forEach(data -> dataSet.add(new QosLevel(data)));
        }
        log.debug("Loaded {} data", dataSet.size());

        for (QosLevel q : dataSet) {
            boolean next = false;
            for (QosLevel otherQ : dataSet) {
                boolean a = domain(otherQ, q);
                if (a) {
                    next = true;
                    break;
                }
            }
            if (next) continue;
            q.l = 0;
        }

        List<QosLevel> pf = dataSet.stream().filter(q -> q.l == 0).collect(Collectors.toList());
        log.debug("Pareto front:");
        StringBuilder pfData = new StringBuilder();
        pf.forEach(data -> {
            log.debug("{}", data);
            pfData.append(data.toNumpy());
            pfData.append('\n');
        });
        Path pfDataFile = WorkDir.getParetoFrontFile(dataset.name(), type);
        Files.write(pfDataFile, pfData.toString().getBytes());

        log.info("Save pareto front data to {}", pfDataFile.getFileName());
    }
}
