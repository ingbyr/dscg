package com.hwsc;


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
import com.ingbyr.hwsc.planner.Planner;
import com.ingbyr.hwsc.planner.PlannerAnalyzer;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.planner.PlannerConfigFile;
import com.ingbyr.hwsc.planner.exception.HWSCConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.jgrapht.GraphPath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Path;
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
            Qos originQos = QosUtils.mergeOriginQos(services);
            log.info("Origin qos: {}", originQos);
        }
    }

    public static void findByCPG(Dataset dataset, int maxPreNodeSize) throws IOException {
        log.info("Finding search space", dataset);
        log.info("Max new pre node size {}", maxPreNodeSize);
        DataSetReader reader = new XMLDataSetReader();
        reader.setDataset(dataset);
        PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);
        CompletePlaningGraph cpg = new CompletePlaningGraph(pg);
        cpg.setMaxPreNodeSize(maxPreNodeSize);
        cpg.build();

        log.info("Generating all path", dataset);
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

        Path dataFile = WorkDir.getSearchSpaceFile(dataset.name(), "cpg");
        FileUtils.write(dataFile.toFile(), data.toString(), Charset.defaultCharset());
        log.info("[{}] Saved to file {}", dataset, dataFile.getFileName());

    }

    public static void findByHWSC(Dataset dataset, int bench) throws IOException, ConfigurationException, NoSuchMethodException, IllegalAccessException, HWSCConfigException, InstantiationException, InvocationTargetException, ClassNotFoundException {

        PlannerConfig config = new PlannerConfigFile();
        config.setDataset(dataset);
        log.debug("{}", config);
        Planner planner = new Planner();
        planner.setup(config, new XMLDataSetReader());

        Path result = WorkDir.getSearchSpaceFile(dataset.name(), "hwsc");
        try (FileOutputStream fos = new FileOutputStream(result.toFile(), true)) {
            for (int b = 0; b < bench; b++) {
                log.info("Bench {}/{}", b + 1, bench);
                planner.exec();
                PlannerAnalyzer analyzer = planner.getAnalyzer();
                Set<String> qos = analyzer.getLastPop().stream().map(ind -> ind.getQos().toNumpy()).collect(Collectors.toSet());
                for (String uniqueQos : qos) {
                    fos.write(uniqueQos.getBytes());
                    fos.write('\n');
                }
            }
        }
        log.info("Save data to {} after {} bench", result.getFileName(), bench);
    }
}
