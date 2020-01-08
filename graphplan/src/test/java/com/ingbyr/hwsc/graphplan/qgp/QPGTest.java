package com.ingbyr.hwsc.graphplan.qgp;

import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

class QPGTest {

    @Test
    void qosWSC() {
        DataSetReader reader = new XmlDatasetReader();
        reader.setDataset(Dataset.wsc2020_01);
        // reader.setDataset(Dataset.wsc2009_01);
        // reader.setDataset(Dataset.wsc2008_02);
        QPG qpg = new QPG(reader);
        qpg.qosWSC();
    }
}