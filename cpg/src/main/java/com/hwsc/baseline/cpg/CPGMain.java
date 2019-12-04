package com.hwsc.baseline.cpg;


import com.hwsc.baseline.cpg.extractors.*;
import com.hwsc.baseline.cpg.models.CompletePlaningGraph;
import com.hwsc.baseline.cpg.models.DWGEdge;
import com.hwsc.baseline.cpg.models.DWGNode;
import com.hwsc.baseline.cpg.models.PlanningGraph;
import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.common.util.WorkDir;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.dataset.util.QosUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CPGMain {

    public static int MAX_NEW_PRE_NODE_SIZE = 256;

    public static void main(String[] args) throws IOException {
        Dataset dataset;

        switch (args.length) {
            case 0:
                dataset = Dataset.wsc2009_01;
                break;
            case 1:
                dataset = Dataset.valueOf(args[0]);
                break;
            case 2:
                dataset = Dataset.valueOf(args[0]);
                MAX_NEW_PRE_NODE_SIZE = Integer.parseInt(args[1]);
                break;
            default:
                throw new RuntimeException("Error args " + Arrays.toString(args));
        }

        DataSetReader reader = new XMLDataSetReader();
        reader.setDataset(dataset);

        PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);

        CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
        cpg.trans();

        log.info("search shortest path by dijkstra...");
        PlanExtractor dijkstraExtractor = new DijkstraExtractor(cpg);

        cpgSearchBatch("dijksta", dijkstraExtractor);

        StringBuilder result = new StringBuilder();
        for (GraphPath<DWGNode, DWGEdge> path : dijkstraExtractor.getPaths()) {
            log.info("shortest path: {}", path);
            double cost = PlanExtractors.calcCost(path);
            log.info("cost: {}", cost);
            List<Service> services = PlanExtractors.getServices(path);
            log.info("services: {}", services);
            log.info("valid: {}", PlanExtractors.validServices(services, reader.getInputSet(), reader.getGoalSet()));
            Qos qos = Qos.getTotalQos(PlanExtractors.getServices(path));
            qos = QosUtils.flip(qos);
            log.info("qos: {}", qos);

            result.append("services: " + services);
            result.append('\n');
            result.append("cost: " + cost);
            result.append('\n');
            result.append("qos: " + qos);
            File logFile = WorkDir.WORK_DIR.resolve("best-qos").resolve(dataset + ".txt").toFile();
            FileUtils.write(logFile, result.toString(), Charset.defaultCharset());
        }
    }


    private static void cpgSearch(PlanningGraph pg) {
        // transfer to graph
        CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
        cpg.trans();

        log.info("search all path...");
        PlanExtractor allPathsExtractor = new AllPathsExtractor(cpg);
        allPathsExtractor.find();
        List<GraphPath<DWGNode, DWGEdge>> allPaths = allPathsExtractor.getPaths();
        PlanExtractors.assertValidPaths(allPaths);


        log.info("search shortest path by dijkstra...");
        PlanExtractor dijkstraExtractor = new DijkstraExtractor(cpg);
        cpgSearchBatch("dijksta", dijkstraExtractor);
        dijkstraExtractor.getPaths().forEach(path -> {
            log.info("shortest path: {}", path);
            log.info("cost: {}", PlanExtractors.calcCost(path));
        });

        log.info("search shortest path by a*...");
        CompletePlaningGraph reverseTransfer = new CompletePlaningGraph(pg);
        reverseTransfer.setReverseGraph(true);
        reverseTransfer.setConceptDistance(pg.delta.distance);
        reverseTransfer.trans();

        AStarAdmissibleHeuristic<DWGNode> heuristic1 = new HSPRHeuristic<>(false);
        PlanExtractor aStartExtractor1 = new AStarExtractor(reverseTransfer, heuristic1);
        cpgSearchBatch("a *", aStartExtractor1);

        AStarAdmissibleHeuristic<DWGNode> heuristic2 = new HSPRHeuristic<>(true);
        PlanExtractor aStartExtractor2 = new AStarExtractor(reverseTransfer, heuristic2);
        cpgSearchBatch("dij from a*", aStartExtractor2);

        List<GraphPath<DWGNode, DWGEdge>> aStartPaths = aStartExtractor1.getPaths();
        aStartPaths.forEach(path -> {
            log.info("shortest path: {}", path);
            log.info("cost: {}", PlanExtractors.calcCost(path));
        });
    }

    private static void cpgSearchBatch(String name, PlanExtractor extractor) {
        log.debug("");
        log.debug("{}", name);
        for (int i = 0; i < 1; i++) {
            extractor.find();
        }
    }
}
