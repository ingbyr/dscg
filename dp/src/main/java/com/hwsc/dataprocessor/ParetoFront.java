package com.hwsc.dataprocessor;

import com.ingbyr.hwsc.common.NumpyDataFormat;
import com.ingbyr.hwsc.common.WorkDir;
import com.ingbyr.hwsc.dataset.Dataset;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class ParetoFront {

    // Forbid creating instance
    private ParetoFront() {
    }

    @ToString
    private static final class QosLevel implements Comparable<QosLevel>, NumpyDataFormat {

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

    public static void find(Dataset dataset) throws IOException {
        List<QosLevel> dataList = new LinkedList<>();
        Path dataPath = WorkDir.getQosSearchSpaceFile(dataset.name());
        log.debug("[{}] Finding pareto front from {}", dataset, dataPath);
        try (Stream<String> fs = Files.lines(dataPath)) {
            fs.forEach(data -> dataList.add(new QosLevel(data)));
        }
        log.debug("load all data {}", dataList.size());

        final int N = dataList.get(0).v.length;
        // TODO move this to fitness
//        List<QoSData> dataCopyList = new LinkedList<>(dataList);
//        int level = 0;
//        int cur = dataList.size();
//        int pre = cur + 1;
//        while (cur < pre) {
//            pre = dataCopyList.size();
//
//            log.debug("current level {}, size {}", level, pre);
//
//            for (QoSData q : dataCopyList) {
//                boolean next = false;
//                for (QoSData otherQ : dataCopyList) {
//                    boolean a = domain(otherQ, q, N);
//                    if (a) {
//                        next = true;
//                        break;
//                    }
//                }
//                if (next) continue;
//                q.l = level;
//            }
//
//            dataCopyList.removeIf(qoSData -> qoSData.l >= 0);
//            cur = dataCopyList.size();
//            level++;
//        }
        // End
        for (QosLevel q : dataList) {
            boolean next = false;
            for (QosLevel otherQ : dataList) {
                boolean a = domain(otherQ, q, N);
                if (a) {
                    next = true;
                    break;
                }
            }
            if (next) continue;
            q.l = 0;
        }

        List<QosLevel> pf = dataList.stream().filter(q -> q.l == 0).collect(Collectors.toList());
        log.debug("[{}] Pareto front:", dataset);
        StringBuilder pfData = new StringBuilder();
        pf.forEach(data -> {
            log.debug("{}", data);
            pfData.append(data.toNumpy());
            pfData.append('\n');
        });
        Files.write(WorkDir.getQosParetoFrontFile(dataset.name()), pfData.toString().getBytes());
    }

    private static boolean domain(QosLevel q1, QosLevel q2, int len) {
        boolean hasBetter = false;
        for (int i = 0; i < len; i++) {
            if (q1.v[i] - q2.v[i] > 0)
                return false;
            else if (q1.v[i] - q2.v[i] < 0)
                hasBetter = true;
        }
        return hasBetter;
    }
}
