package com.ingbyr.dscg.planner;

import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import com.ingbyr.dscg.HeuristicInfo;
import org.junit.jupiter.api.Test;

class TpgTest {

    @Test
    void solve() {
        DataSetReader reader = new XmlDatasetReader(Dataset.wsc2020_01);
        HeuristicInfo h = new HeuristicInfo();
        h.setup(reader);
        Planner planner = new Tpg();
        planner.setConceptMap(h.getConceptMap());
        planner.setServiceMap(h.getServiceMap());
        Solution solution = planner.solve(reader.getInputSet(), reader.getGoalSet(), 1);
        System.out.println(solution);
    }
}