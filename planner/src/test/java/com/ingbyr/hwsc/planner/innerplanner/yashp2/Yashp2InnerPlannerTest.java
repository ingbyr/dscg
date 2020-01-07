package com.ingbyr.hwsc.planner.innerplanner.yashp2;

import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import com.ingbyr.hwsc.planner.HeuristicInfo;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.Solution;
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
    void solve() {
        DataSetReader reader = new XmlDatasetReader(Dataset.wsc2009_01);
        HeuristicInfo h = new HeuristicInfo();
        h.setup(reader);
        InnerPlanner innerPlanner = new InnerPlannerYashp2(h.getServiceMap(), h.getConceptMap(), 1);
        Solution solution = innerPlanner.solve(reader.getInputSet(), reader.getGoalSet(), 1);
        System.out.println(solution);
    }
}