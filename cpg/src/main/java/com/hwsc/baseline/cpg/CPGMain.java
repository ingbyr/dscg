package com.hwsc.baseline.cpg;


import com.hwsc.baseline.cpg.extractors.AllPathsExtractor;
import com.hwsc.baseline.cpg.extractors.PlanExtractor;
import com.hwsc.baseline.cpg.extractors.PlanExtractors;
import com.hwsc.baseline.cpg.models.CompletePlaningGraph;
import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;
import com.hwsc.baseline.cpg.models.PlanningGraph;
import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.common.util.QosUtils;
import com.ingbyr.hwsc.common.util.WorkDir;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jgrapht.GraphPath;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class CPGMain {

    public static int MAX_NEW_PRE_NODE_SIZE = 64;

    public static void main(String[] args) throws IOException {

        Dataset[] datasets = new Dataset[]{
                Dataset.wsc2008_01,
//                Dataset.wsc2008_02,
//                Dataset.wsc2008_03,
//                Dataset.wsc2008_04,
//                Dataset.wsc2008_05,
//                Dataset.wsc2008_06,
//                Dataset.wsc2009_01,
//                Dataset.wsc2009_02,
//                Dataset.wsc2009_03,
//                Dataset.wsc2009_04
        };

        StringBuilder result = new StringBuilder();
//        String prefix = "best_";
        String prefix = "pareto_";
        File logFile = WorkDir.WORK_DIR.resolve("best-qos").resolve(prefix + Arrays.toString(Qos.ACTIVE_TYPES) + ".txt").toFile();

        for (Dataset dataset : datasets) {
            DataSetReader reader = new XMLDataSetReader();
            reader.setDataset(dataset);

            PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);

            CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
            cpg.trans();

            log.info("process {}", dataset);

//            process(new DijkstraExtractor(cpg), dataset, reader, result);

            adjustCostOfServices(reader);
            findPareto(cpg, result);
        }

        FileUtils.write(logFile, result.toString(), Charset.defaultCharset());
    }

    public static void process(PlanExtractor extractor, Dataset dataset, DataSetReader reader, StringBuilder result) {
        extractor.find();
        for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
            log.info("path: {}", path);
            double cost = PlanExtractors.calcCost(path);
            log.info("cost: {}", cost);
            List<Service> services = PlanExtractors.getServices(path);
            log.info("services: {}", services);
            log.info("valid: {}", PlanExtractors.validServices(services, reader.getInputSet(), reader.getGoalSet()));
            Qos scaledQos = QosUtils.mergeQos(services);
            log.info("scaled qos: {}", scaledQos);
            Qos originQos = QosUtils.mergeOriginQos(services);
            log.info("origin qos: {}", originQos);

            result.append("[" + dataset + "] services: " + services);
            result.append("\n[" + dataset + "] cost: " + cost);
            result.append("\n[" + dataset + "] scaled qos: " + scaledQos);
            result.append("\n[" + dataset + "] origin qos: " + originQos);

            result.append("\n\n");
        }
    }

    public static void findPareto(CompletePlaningGraph cpg, StringBuilder result) {
        log.info("find pareto");
        PlanExtractor extractor = new AllPathsExtractor(cpg);
        extractor.find();
        Qos bQos = new Qos(Double.MAX_VALUE);
        for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
            List<Service> services = PlanExtractors.getServices(path);
            log.debug("Service {}", services);
            Qos qos = QosUtils.mergeQos(services);
            log.debug("Qos {}", qos);
            log.debug("bQos {}", bQos);
            if (bQos.compareTo(qos) > 0) {
                bQos = qos;
                result.append(qos);
                result.append(',');
            }
        }
        result.append('\n');
    }

    private static void adjustCostOfServices(DataSetReader reader) {
        for (Map.Entry<String, Service> entry : reader.getServiceMap().entrySet()) {
            Service service = entry.getValue();
            double newCost = 0.0;
            Qos scaledQos = service.getQos();
            for (int qosType : Qos.ACTIVE_TYPES) {
                newCost += scaledQos.get(qosType);
            }
            service.setCost(newCost);
        }
    }
}
