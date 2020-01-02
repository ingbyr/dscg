package com.hwsc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwsc.extractors.AllPathsExtractor;
import com.hwsc.extractors.DijkstraExtractor;
import com.hwsc.extractors.PlanExtractor;
import com.hwsc.extractors.PlanExtractors;
import com.hwsc.models.CompletePlaningGraph;
import com.hwsc.models.DWGEdge;
import com.hwsc.models.DWGNode;
import com.hwsc.models.PlanningGraph;
import com.hwsc.searching.GeneratePlanningGraph;
import com.ingbyr.hwsc.common.*;
import com.ingbyr.hwsc.planner.*;
import com.ingbyr.hwsc.planner.exception.HWSCConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jgrapht.GraphPath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SearchSpace {

    public static void findBestQoS(Dataset dataset, int maxPreNodeSize) {
        DataSetReader reader = new XMLDataSetReader();
        reader.setDataset(dataset);

        PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);

        CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
        cpg.setMaxPreNodeSize(maxPreNodeSize);
        cpg.build();

        log.info("Try to find best from {} by dijkstra", dataset);
        PlanExtractor extractor = new DijkstraExtractor(cpg);
        extractor.find();
        for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
            double cost = PlanExtractors.calcCost(path);
            log.info("Cost: {}", cost);
            List<Service> services = PlanExtractors.getServices(path);
            log.info("Services: {}", services);
            Qos scaledQos = QosUtils.mergeQos(services);
            log.info("Scaled qos: {}", scaledQos);
            Qos originQos = QosUtils.mergeRawQos(services);
            log.info("Origin qos: {}", originQos);
        }
    }

    public static void findByCPG(Dataset dataset, int maxPreNodeSize) throws IOException {
        log.info("Finding search space");
        log.info("Max new pre node size {}", maxPreNodeSize);
        DataSetReader reader = new XMLDataSetReader();
        reader.setDataset(dataset);
        PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);
        CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
        cpg.setMaxPreNodeSize(maxPreNodeSize);
        cpg.build();

        log.info("Generating all path");
        PlanExtractor extractor = new AllPathsExtractor(cpg);
        extractor.find();

        StringBuilder data = new StringBuilder();
        for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
            List<Service> services = PlanExtractors.getServices(path);
            log.trace("Service {}", services);
            Qos qos = QosUtils.mergeQos(services);
            log.trace("Qos {}", qos);
            data.append(qos.toNumpy());
            data.append("\n");
        }

        Path dataFile = WorkDir.getSearchSpaceFile(dataset.name());
        Files.write(dataFile, data.toString().getBytes());
        log.info("[{}] Saved to file {}", dataset, dataFile.getFileName());

    }

    public static void findByHWSC(Dataset dataset, int bench) throws IOException, ConfigurationException, NoSuchMethodException, IllegalAccessException, HWSCConfigException, InstantiationException, InvocationTargetException, ClassNotFoundException {

        PlannerConfig config = new PlannerConfigFile();
        config.setDataset(dataset);
        log.debug("{}", config);
        Planner planner = new Planner();
        planner.setup(config, new XMLDataSetReader());

        log.info("Warm up search");
        for (int i = 0; i < 3; i++) {
            planner.exec();
        }

        // Start bench test
        Path searchSpaceFile = WorkDir.getSearchSpaceFile(dataset.name());
        Path rawSearchSpaceFile = WorkDir.getRawSearchSpaceFile(dataset.name());
        List<PlannerResult> plannerResultList = new LinkedList<>();
        try (FileOutputStream fos = new FileOutputStream(searchSpaceFile.toFile(), true);
             FileOutputStream rfos = new FileOutputStream(rawSearchSpaceFile.toFile(), true)) {
            for (int b = 0; b < bench; b++) {
                log.info("Bench {}/{}", b + 1, bench);
                planner.exec();

                PlannerAnalyzer analyzer = planner.getAnalyzer();

                Set<String> qos = analyzer.getLastPop().stream().map(ind -> ind.getQos().toNumpy()).collect(Collectors.toSet());
                for (String q : qos) {
                    fos.write(q.getBytes());
                    fos.write('\n');
                }
                fos.write('\n');

                Set<String> rqos = analyzer.getLastPop().stream().map(ind -> ind.getRawQos().toNumpy()).collect(Collectors.toSet());
                for (String rq : rqos) {
                    rfos.write(rq.getBytes());
                    rfos.write('\n');
                }
                rfos.write('\n');

                PlannerResult plannerResult = analyzer.getResult();
                plannerResult.bench = b;
                plannerResultList.add(plannerResult);

            }
        }

        ObjectMapper mapper = new ObjectMapper();
        Path plannerBenchFile = WorkDir.getPlannerBenchFile(dataset.name());
        mapper.writeValue(plannerBenchFile.toFile(), plannerResultList);
        log.info("Save data to {}", searchSpaceFile.getFileName());
        log.info("Save bench info to {}", plannerBenchFile.getFileName());
    }
}
