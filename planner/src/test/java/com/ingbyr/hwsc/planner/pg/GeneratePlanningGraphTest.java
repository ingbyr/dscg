package com.ingbyr.hwsc.planner.pg;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.planner.model.PlanningGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeneratePlanningGraphTest {

    @Test
    @DisplayName("Generate planning graph from WSDL")
    void fromWSDL() {
        PlanningGraph pg = GeneratePlanningGraph.fromWSDL(Dataset.wsc2009_01);
        System.out.println(pg);
    }

    @Test
    @DisplayName("Generate planning graph from XML")
    void fromXML() {
        PlanningGraph pg = GeneratePlanningGraph.fromXML(Dataset.wsc2009_01);
        System.out.println(pg);
    }
}