package com.hwsc.baseline.cpg;


import com.hwsc.baseline.cpg.extractors.AllPathsExtractor;
import com.hwsc.baseline.cpg.extractors.DijkstraExtractor;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CPGMain {

    public static int MAX_NEW_PRE_NODE_SIZE = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            MAX_NEW_PRE_NODE_SIZE = Integer.parseInt(args[0]);
        }
        log.info("max new node limit: {}", MAX_NEW_PRE_NODE_SIZE);
        List<String> datasetNames = Files.readAllLines(Paths.get(System.getProperty("user.dir")).resolve("datasets.txt"));
        List<Dataset> datasets = datasetNames.stream()
                .filter(s -> !s.startsWith("/"))
                .map(Dataset::valueOf)
                .collect(Collectors.toList());
        findSearchSpace(datasets);
    }

    public static void findBestQoS(List<Dataset> datasets) throws IOException {
        File logFile = WorkDir.WORK_DIR.resolve("best-qos").resolve("best_" + Arrays.toString(Qos.ACTIVE_TYPES) + ".txt").toFile();
        StringBuilder result = new StringBuilder();

        for (Dataset dataset : datasets) {
            DataSetReader reader = new XMLDataSetReader();
            reader.setDataset(dataset);
            adjustCostOfServices(reader);

            PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);

            CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
            cpg.trans();

            log.info("process {}", dataset);
            PlanExtractor extractor = new DijkstraExtractor(cpg);
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

        FileUtils.write(logFile, result.toString(), Charset.defaultCharset());
    }

    public static void findSearchSpace(List<Dataset> datasets) throws IOException {
        log.info("try to find search space");

        for (Dataset dataset : datasets) {
            log.info("process {}", dataset);
            File logFile = WorkDir.WORK_DIR.resolve("best-qos").resolve(dataset + "_pareto_" + Arrays.toString(Qos.ACTIVE_TYPES) + ".txt").toFile();
            StringBuilder result = new StringBuilder();
            DataSetReader reader = new XMLDataSetReader();
            reader.setDataset(dataset);
            PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);
            CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
            cpg.trans();

            adjustCostOfServices(reader);
            log.info("find all path");
            PlanExtractor extractor = new AllPathsExtractor(cpg);
            extractor.find();

            log.info("save all path to file");
            for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
                List<Service> services = PlanExtractors.getServices(path);
                log.debug("Service {}", services);
                Qos qos = QosUtils.mergeQos(services);
                log.debug("Qos {}", qos);
                result.append(qos.toString());
                result.append(",\n");
            }
            FileUtils.write(logFile, result.toString(), Charset.defaultCharset());
        }
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
