package com.ingbyr.hwsc.pg;

import com.ingbyr.hwsc.model.PlanningGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeneratePlanningGraphTest {

    @Test
    @DisplayName("Generate planning graph from WSDL")
    void fromWSDL() {
        PlanningGraph pg = GeneratePlanningGraph.fromWSDL("2009", "01");
        System.out.println(pg);
    }

    @Test
    @DisplayName("Generate planning graph from XML")
    void fromXML() {
        PlanningGraph pg = GeneratePlanningGraph.fromXML("2009", "01");
        System.out.println(pg);
    }
}