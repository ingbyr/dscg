package com.ingbyr.hwsc.graphplan.qgp;

import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeamTPGTest {

    @Test
    void beamSearch() {
        DataSetReader reader = new XmlDatasetReader();
        // reader.setDataset(Dataset.wsc2020_01);
        // reader.setDataset(Dataset.wsc2008_01);
        reader.setDataset(Dataset.wsc2009_05);
        BeamTPG beamTPG = new BeamTPG(reader);
        beamTPG.beamSearch();
    }
}