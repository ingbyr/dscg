package com.ingbyr.hwsc.planner.innerplanner.cpg;

import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.planner.PlannerConfigFile;
import com.ingbyr.hwsc.planner.innerplanner.cpg.extractors.DijkstraExtractor;
import com.ingbyr.hwsc.planner.innerplanner.cpg.extractors.PlanExtractor;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.CompletePlaningGraph;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.PlanningGraph;
import com.ingbyr.hwsc.planner.pg.GeneratePlanningGraph;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class CPGMain {
    public static void main(String[] args) throws ConfigurationException {
        PlannerConfig config = new PlannerConfigFile();
        PlanningGraph pg = GeneratePlanningGraph.fromXML(config.getDataset());
        CompletePlaningGraph cpg = new CompletePlaningGraph();
        cpg.build(pg);
        PlanExtractor dijkstraExtractor = new DijkstraExtractor();
        dijkstraExtractor.setCpg(cpg);
        dijkstraExtractor.find();
    }
}
