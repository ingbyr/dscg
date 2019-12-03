package com.hwsc.baseline.cpg;

import com.hwsc.baseline.cpg.searching.ForwardPlanningGraphSearcher;
import com.hwsc.baseline.cpg.searching.PlanningGraphSearcher;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.WSDLDataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import lombok.extern.slf4j.Slf4j;
import com.hwsc.baseline.cpg.models.PlanningGraph;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

@Slf4j
public class GeneratePlanningGraph {

    public static PlanningGraph generatePlanningGraph(DataSetReader dataSetReader) {
        PlanningGraph pg = new PlanningGraph(dataSetReader.getConceptMap());
        pg.addPLevel(dataSetReader.getInputSet());
        pg.setGoalSet(dataSetReader.getGoalSet());
        pg.addALevel(new HashSet<>());
        PlanningGraphSearcher searcher = new ForwardPlanningGraphSearcher(pg, dataSetReader.getConceptMap(), dataSetReader.getServiceMap());
        searcher.search();
        displayPlanningGraph(pg, searcher);
        return pg;
    }

    private static void displayPlanningGraph(PlanningGraph planningGraph, PlanningGraphSearcher search) {
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
