package com.hwsc.bench;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.WorkDir;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import com.ingbyr.hwsc.graphplan.qgp.BeamTPG;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TaggedPlanGraph {

    static List<BeamTPG.CombService> combServiceList = new LinkedList<>();

    public static void find(Dataset dataset, int beamWidth, int bench) throws IOException {
        for (int i = 0; i < bench; i++) {
            log.debug("Bench {}/{}", i, bench);
            find(dataset, beamWidth);
        }

        Path benchFile = WorkDir.getBenchFile(dataset.name(), "TPG");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(benchFile.toFile(), combServiceList);
        log.info("Save bench info to {}", benchFile.getFileName());
    }

    private static void find(Dataset dataset, int beamWidth) {

        DataSetReader reader = new XmlDatasetReader();
        reader.setDataset(dataset);
        BeamTPG tpg = new BeamTPG(reader);
        BeamTPG.BEAM_WIDTH = beamWidth;
        BeamTPG.CombService combService = tpg.beamSearch();
        System.out.println(combService);
        combServiceList.add(combService);


    }
}
