package com.ingbyr.hwsc.planner.innerplanner.yashp2;

import com.ingbyr.hwsc.planner.PlannerAnalyzer;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
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
class Yashp2InnerPlannerTest {

    @Test
    void openNode() {
        InnerPlannerYashp2.YNode n1 = InnerPlannerYashp2.YNode.builder().heuristic(1).build();
        InnerPlannerYashp2.YNode n2 = InnerPlannerYashp2.YNode.builder().heuristic(2).build();
        InnerPlannerYashp2.YNode n3 = InnerPlannerYashp2.YNode.builder().heuristic(3).build();
        PriorityQueue<InnerPlannerYashp2.YNode> open = new PriorityQueue<>();
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
        InnerPlanner innerPlanner = new InnerPlannerYashp2(reader.getServiceMap(),reader.getConceptMap(),1);
        Solution solution = innerPlanner.solve(reader.getInputSet(), reader.getGoalSet(), 1);
        PlannerAnalyzer.checkSolution(reader.getInputSet(), reader.getGoalSet(), solution.services);
    }
}