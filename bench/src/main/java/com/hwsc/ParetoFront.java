package com.hwsc;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.NumpyDataFormat;
import com.ingbyr.hwsc.common.WorkDir;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static void find(Dataset dataset) throws IOException {
        findFromFileData(WorkDir.getSearchSpaceFile(dataset.name()),
                WorkDir.getParetoFrontFile(dataset.name()),
                dataset);

        findFromFileData(WorkDir.getRawSearchSpaceFile(dataset.name()),
                WorkDir.getRawParetoFrontFile(dataset.name()),
                dataset);
    }

    private static void findFromFileData(Path sourceFile, Path destFile, Dataset dataset) throws IOException {
        Set<QosLevel> dataSet = new HashSet<>();
        log.info("Load search space from {}", sourceFile.getFileName());
        try (Stream<String> fs = Files.lines(sourceFile)) {
            fs.filter(StringUtils::isNoneBlank).forEach(data -> dataSet.add(new QosLevel(data)));
        }

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
        Files.write(destFile, pfData.toString().getBytes());

        log.info("Save pareto front data to {}", destFile.getFileName());
    }
}
