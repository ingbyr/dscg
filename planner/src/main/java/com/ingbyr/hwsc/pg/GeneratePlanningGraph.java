package com.ingbyr.hwsc.pg;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dataset.reader.DataSetReader;
import com.ingbyr.hwsc.dataset.reader.WSDLDataSetReader;
import com.ingbyr.hwsc.dataset.reader.XMLDataSetReader;
import com.ingbyr.hwsc.model.PlanningGraph;
import com.ingbyr.hwsc.pg.searching.ForwardPlanningGraphSearcher;
import com.ingbyr.hwsc.pg.searching.PlanningGraphSearcher;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

@Slf4j
public class GeneratePlanningGraph {
    /**
     * Generate planning graph from wsdl file which has no QOS
     *
     * @param datasetId Data set id (01 - 05)
     * @return PlanningGraph
     */
    public static PlanningGraph fromWSDL(String dataset, String datasetId) {
        DataSetReader dataSetReader = initDataSetReader(new WSDLDataSetReader(dataset, datasetId));
        return generatePlanningGraph(dataSetReader);
    }

    /**
     * Generate planning graph from xml file
     */
    public static PlanningGraph fromXML(String dataset, String datasetId) {
        DataSetReader dataSetReader = initDataSetReader(new XMLDataSetReader(dataset, datasetId));
        return generatePlanningGraph(dataSetReader);
    }

    private static DataSetReader initDataSetReader(DataSetReader dataSetReader) {
        dataSetReader.process();
        log.debug("Parsing Successfully Completed");
        return dataSetReader;
    }

    private static PlanningGraph generatePlanningGraph(DataSetReader dataSetReader) {
        PlanningGraph pg = new PlanningGraph(dataSetReader.getConceptMap());

        pg.addPLevel(new HashSet<>(dataSetReader.getInputSet()));
        pg.setInputSet(dataSetReader.getInputSet());
        pg.setGoalSet(dataSetReader.getGoalSet());
        pg.setServiceMap(dataSetReader.getServiceMap());
        pg.addALevel(new HashSet<>());

        PlanningGraphSearcher searcher = new ForwardPlanningGraphSearcher(pg, dataSetReader.getConceptMap(), dataSetReader.getServiceMap());
        searcher.search();

        displayPlanningGraph(pg, searcher);
        return pg;
    }

    public static void displayPlanningGraph(PlanningGraph planningGraph, PlanningGraphSearcher search) {
        int levelCount = 0;
        LinkedList<LinkedHashSet<Concept>> propLevels = new LinkedList<>(planningGraph.propLevels);
        log.debug("Input Parameters : {}", propLevels.get(0));
        log.debug("Goal : {}", planningGraph.getGoalSet());
        log.debug("====================================================================");
        log.debug("Input Concepts: {}", propLevels.get(0));
        log.debug("Goal Concepts: {}", planningGraph.getGoalSet());
        for (LinkedHashSet<Service> aLevel : planningGraph.actionLevels) {
            log.debug("******************************Level {}******************************", ++levelCount);
            log.debug("Services: {}", aLevel);
            log.debug("Number of services: {}", aLevel.size());
            log.debug("Parameters: {}", propLevels.get(levelCount));
            log.debug("Number of Parameters: {}", propLevels.get(levelCount).size());
        }
        log.debug("====================================================================");
        log.debug("Forward search time was {} ms.", search.getComposeTime());
        log.debug("Number of levels: {}", levelCount);
        log.debug("Back Tracking time : {} ms.", search.getBackTrackingTime());
        log.debug("====================================================================");
    }

}
