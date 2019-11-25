package com.ingbyr.hwsc.planner.planner.yashp2;

import com.ingbyr.hwsc.planner.DAEAnalyzer;
import com.ingbyr.hwsc.planner.Planner;
import com.ingbyr.hwsc.planner.Solution;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.exception.NotValidSolutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

/**
 * @author ingbyr
 */
class Yashp2PlannerTest {

    @Test
    void openNode() {
        Yashp2Planner.YNode n1 = Yashp2Planner.YNode.builder().heuristic(1).build();
        Yashp2Planner.YNode n2 = Yashp2Planner.YNode.builder().heuristic(2).build();
        Yashp2Planner.YNode n3 = Yashp2Planner.YNode.builder().heuristic(3).build();
        PriorityQueue<Yashp2Planner.YNode> open = new PriorityQueue<>();
        open.add(n2);
        open.add(n1);
        open.add(n3);
        Assertions.assertEquals(open.poll(), n1);
        Assertions.assertEquals(open.poll(), n2);
        Assertions.assertEquals(open.poll(), n3);
    }

    @Test
    void solve() throws NotValidSolutionException {
        DataSetReader reader = new XMLDataSetReader(Dataset.wsc2009_01);
        reader.process();
        Planner planner = new Yashp2Planner(reader.getServiceMap(),reader.getConceptMap(),1);
        Solution solution = planner.solve(reader.getInputSet(), reader.getGoalSet(), 1);
        DAEAnalyzer.checkSolution(reader.getInputSet(), reader.getGoalSet(), solution.services);
    }
}