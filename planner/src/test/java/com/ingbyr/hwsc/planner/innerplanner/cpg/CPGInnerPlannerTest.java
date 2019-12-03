package com.ingbyr.hwsc.planner.innerplanner.cpg;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.planner.Solution;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.cpg.extractors.*;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.CompletePlaningGraph;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.DWGEdge;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.DWGNode;
import com.ingbyr.hwsc.planner.innerplanner.cpg.models.PlanningGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ingbyr.hwsc.planner.innerplanner.cpg.extractors.PlanExtractors.assertValidPaths;

class CPGInnerPlannerTest {

    private PlanningGraph pg = GeneratePlanningGraph.fromXML(Dataset.wsc2009_01);

    @Test
    void solveByAllPath() {
        CompletePlaningGraph cpg = new CompletePlaningGraph();
        cpg.build(pg);
        PlanExtractor allPathsExtractor = new AllPathsExtractor();
        allPathsExtractor.setCpg(cpg);
        allPathsExtractor.find();
        List<GraphPath<DWGNode, DWGEdge>> allPaths = allPathsExtractor.getPaths();
        assertValidPaths(allPaths);
    }

    @Test
    void solveByDijkstra() {
        CompletePlaningGraph cpg = new CompletePlaningGraph();
        cpg.build(pg);
        PlanExtractor dijkstraExtractor = new DijkstraExtractor();
        dijkstraExtractor.setCpg(cpg);
        dijkstraExtractor.find();
    }

    @Test
    void solveByAStar() {
        CompletePlaningGraph reversedCpg = new CompletePlaningGraph();
        reversedCpg.setReverseGraph(true);
        reversedCpg.setConceptDistance(pg.delta.distance);
        reversedCpg.build(pg);

        AStarAdmissibleHeuristic<DWGNode> heuristic1 = new HSPRHeuristic<>(false);
        PlanExtractor aStartExtractor = new AStarExtractor(heuristic1);
        aStartExtractor.setCpg(reversedCpg);
        aStartExtractor.setName("a star");
        aStartExtractor.find();

        AStarAdmissibleHeuristic<DWGNode> heuristic2 = new HSPRHeuristic<>(true);
        PlanExtractor aStartExtractorAsDij = new AStarExtractor(heuristic2);
        aStartExtractorAsDij.setCpg(reversedCpg);
        aStartExtractorAsDij.setName("a start as dijkstra");
        aStartExtractorAsDij.find();
    }

    @Test
    void solve() {
        InnerPlanner innerPlanner = new InnerPlannerCPG(pg.getServiceMap(), pg.getConceptMap(), new DijkstraExtractor());
        Solution solution = innerPlanner.solve(pg.getInputSet(), pg.getGoalSet(), 1000);
        System.out.println(solution);
    }
}