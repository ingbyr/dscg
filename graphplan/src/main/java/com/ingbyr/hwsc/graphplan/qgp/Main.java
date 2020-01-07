package com.ingbyr.hwsc.graphplan.qgp;

import com.ingbyr.hwsc.common.*;
import com.ingbyr.hwsc.graphplan.qgp.extractors.DijkstraExtractor;
import com.ingbyr.hwsc.graphplan.qgp.extractors.PlanExtractor;
import com.ingbyr.hwsc.graphplan.qgp.extractors.PlanExtractors;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGEdge;
import com.ingbyr.hwsc.graphplan.qgp.models.DWGNode;
import com.ingbyr.hwsc.graphplan.qgp.models.PlanningGraph;
import com.ingbyr.hwsc.graphplan.qgp.searching.GeneratePlanningGraph;
import org.jgrapht.GraphPath;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataSetReader reader = new XmlDatasetReader();
        reader.setDataset(Dataset.wsc2009_01);

        PlanningGraph pg = GeneratePlanningGraph.generatePlanningGraph(reader);

//        QosGraphPlanner planner = new QosGraphPlanner(pg);
//        planner.setMaxPreNodeSize(4);
//        planner.build();
//
//        log.info("Try to find best from {} by dijkstra", dataset);
//        PlanExtractor extractor = new DijkstraExtractor(planner);
//        extractor.find();
//        for (GraphPath<DWGNode, DWGEdge> path : extractor.getPaths()) {
//            double cost = PlanExtractors.calcCost(path);
//            log.info("Cost: {}", cost);
//            List<Service> services = PlanExtractors.getServices(path);
//            log.info("Services: {}", services);
//            Qos scaledQos = QosUtils.mergeQos(services);
//            log.info("Scaled qos: {}", scaledQos);
//            Qos originQos = QosUtils.mergeRawQos(services);
//            log.info("Origin qos: {}", originQos);
//        }
    }
}
