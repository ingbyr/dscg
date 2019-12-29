package com.hwsc.dataprocessor;


import com.hwsc.dataprocessor.extractors.AllPathsExtractor;
import com.hwsc.dataprocessor.extractors.DijkstraExtractor;
import com.hwsc.dataprocessor.extractors.PlanExtractor;
import com.hwsc.dataprocessor.extractors.PlanExtractors;
import com.hwsc.dataprocessor.models.CompletePlaningGraph;
import com.hwsc.dataprocessor.models.DWGEdge;
import com.hwsc.dataprocessor.models.DWGNode;
import com.hwsc.dataprocessor.models.PlanningGraph;
import com.hwsc.dataprocessor.searching.GeneratePlanningGraph;
import com.ingbyr.hwsc.common.Qos;
import com.ingbyr.hwsc.common.QosUtils;
import com.ingbyr.hwsc.common.Service;
import com.ingbyr.hwsc.common.WorkDir;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XMLDataSetReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jgrapht.GraphPath;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Slf4j
public class CompleteGraph {

    public static int MAX_NEW_PRE_NODE_SIZE;

    static {
        String nodeSize = System.getenv("HWSC_CG_MAX_PRE_NODE");
        try {
            MAX_NEW_PRE_NODE_SIZE = Integer.parseInt(nodeSize);
        } catch (NumberFormatException e) {
            MAX_NEW_PRE_NODE_SIZE = Integer.MAX_VALUE;
            log.info("You can set max new pre node size by \"HWSC_CG_MAX_PRE_NODE\"");
        }
        log.info("Max new pre node limit: {}", MAX_NEW_PRE_NODE_SIZE);
    }

    // FIXME Dir bug
    public static void findBestQoS(List<Dataset> datasetList) throws IOException {
        File logFile = WorkDir.WORK_DIR.resolve("best-qos")
                .resolve("best_" + String.join("_", Qos.NAMES) + ".txt")
                .toFile();
        StringBuilder result = new StringBuilder();

        for (Dataset dataset : datasetList) {
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

    public static void findSearchSpace(Dataset dataset) throws IOException {
        log.info("[{}] Finding search space", dataset);
        File dataFile = WorkDir.getRawQosSearchSpaceFile(dataset.name()).toFile();
        StringBuilder data = new StringBuilder();
        DataSetReader reader = new XMLDataSetReader();
        reader.setDataset(dataset);
        PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);
        CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
        cpg.trans();

        adjustCostOfServices(reader);
        log.info("[{}] Generating all path", dataset);
        PlanExtractor extractor = new AllPathsExtractor(cpg);
        extractor.find();

        for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
            List<Service> services = PlanExtractors.getServices(path);
            log.trace("Service {}", services);
            Qos qos = QosUtils.mergeQos(services);
            log.trace("Qos {}", qos);
            data.append(qos.toNumpy());
            data.append("\n");
        }
        FileUtils.write(dataFile, data.toString(), Charset.defaultCharset());
        log.info("[{}] Saved to file {}", dataset, dataFile);

    }

    private static void adjustCostOfServices(DataSetReader reader) {
        for (Map.Entry<String, Service> entry : reader.getServiceMap().entrySet()) {
            Service service = entry.getValue();
            double newCost = 0.0;
            Qos scaledQos = service.getQos();
            for (int qosType : Qos.TYPES) {
                newCost += scaledQos.get(qosType);
            }
            service.setCost(newCost);
        }
    }
}
