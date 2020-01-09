package com.ingbyr.dscg.planner;

import com.ingbyr.dscg.HeuristicInfo;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

/**
 * @author ingbyr
 */
class Yashp2InnerDscgTest {

    @Test
    void openNode() {
        Yashp2.YNode n1 = Yashp2.YNode.builder().heuristic(1).build();
        Yashp2.YNode n2 = Yashp2.YNode.builder().heuristic(2).build();
        Yashp2.YNode n3 = Yashp2.YNode.builder().heuristic(3).build();
        PriorityQueue<Yashp2.YNode> open = new PriorityQueue<>();
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
        Planner planner = new Yashp2();
        planner.setConceptMap(h.getConceptMap());
        planner.setServiceMap(h.getServiceMap());
        Solution solution = planner.solve(reader.getInputSet(), reader.getGoalSet(), 1);
        System.out.println(solution);
    }
}