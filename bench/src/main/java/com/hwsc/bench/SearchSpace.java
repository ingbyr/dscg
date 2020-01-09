package com.hwsc.bench;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingbyr.dscg.*;
import com.ingbyr.hwsc.common.*;
import com.ingbyr.hwsc.planner.*;
import com.ingbyr.dscg.exception.HWSCConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SearchSpace {

    // public static void findBestQoS(Dataset dataset, int maxPreNodeSize) {
    //     DataSetReader reader = new XmlDatasetReader();
    //     reader.setDataset(dataset);
    //
    //     PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);
    //
    //     CPG planner = new CPG(pg);
    //     planner.setMaxPreNodeSize(maxPreNodeSize);
    //     planner.build();
    //
    //     log.info("Try to find best from {} by dijkstra", dataset);
    //     PlanExtractor extractor = new DijkstraExtractor(planner);
    //     extractor.find();
    //     for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
    //         double cost = PlanExtractors.calcCost(path);
    //         log.info("Cost: {}", cost);
    //         List<Service> services = PlanExtractors.getServices(path);
    //         log.info("Services: {}", services);
    //         Qos scaledQos = QosUtils.mergeQos(services);
    //         log.info("Scaled qos: {}", scaledQos);
    //         Qos originQos = QosUtils.mergeRawQos(services);
    //         log.info("Origin qos: {}", originQos);
    //     }
    // }
    //
    // public static void findByCPG(Dataset dataset, int maxPreNodeSize) throws IOException {
    //     log.info("Finding search space");
    //     log.info("Max new pre node size {}", maxPreNodeSize);
    //     DataSetReader reader = new XmlDatasetReader();
    //     reader.setDataset(dataset);
    //     PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);
    //     CPG cpg = new CPG(pg);
    //     cpg.setMaxPreNodeSize(maxPreNodeSize);
    //     cpg.build();
    //
    //     log.info("Generating all path");
    //     PlanExtractor extractor = new AllPathsExtractor(cpg);
    //     extractor.find();
    //
    //     StringBuilder data = new StringBuilder();
    //     for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
    //         List<Service> services = PlanExtractors.getServices(path);
    //         log.trace("Service {}", services);
    //         Qos qos = QosUtils.mergeQos(services);
    //         log.trace("Qos {}", qos);
    //         data.append(qos.toNumpy());
    //         data.append("\n");
    //     }
    //
    //     Path dataFile = WorkDir.getSearchSpaceFile("cpg_" + dataset.name());
    //     Files.write(dataFile, data.toString().getBytes());
    //     log.info("[{}] Saved to file {}", dataset, dataFile.getFileName());
    //
    // }

    public static void findByHWSC(Dataset dataset, int bench) throws IOException, ConfigurationException, NoSuchMethodException, IllegalAccessException, HWSCConfigException, InstantiationException, InvocationTargetException, ClassNotFoundException {

        DscgConfig config = new DscgConfigFile();
        config.setDataset(dataset);
        log.debug("{}", config);
        Dscg DSCG = new Dscg();
        DSCG.setup(config, new XmlDatasetReader());

        log.info("Warm up search");
        for (int i = 0; i < 3; i++) {
            DSCG.exec();
        }

        // Start bench test
        Path searchSpaceFile = WorkDir.getSearchSpaceFile(dataset.name());
        Path rawSearchSpaceFile = WorkDir.getRawSearchSpaceFile(dataset.name());
        List<BenchStepResult> benchStepResultList = new LinkedList<>();
        try (FileOutputStream fos = new FileOutputStream(searchSpaceFile.toFile(), true);
             FileOutputStream rfos = new FileOutputStream(rawSearchSpaceFile.toFile(), true)) {
            for (int b = 0; b < bench; b++) {
                log.info("Bench {}/{}", b + 1, bench);
                DSCG.exec();

                DscgAnalyzer analyzer = DSCG.getAnalyzer();

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

                BenchStepResult benchStepResult = analyzer.getBenchResult();
                benchStepResult.bench = b;
                benchStepResultList.add(benchStepResult);

            }
        }

        ObjectMapper mapper = new ObjectMapper();
        Path plannerBenchFile = WorkDir.getPlannerBenchFile(dataset.name());
        mapper.writeValue(plannerBenchFile.toFile(), benchStepResultList);
        log.info("Save data to {}", searchSpaceFile.getFileName());
        log.info("Save bench info to {}", plannerBenchFile.getFileName());
    }
}
