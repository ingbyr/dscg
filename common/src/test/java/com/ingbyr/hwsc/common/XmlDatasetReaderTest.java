package com.ingbyr.hwsc.common;

import org.junit.jupiter.api.Test;


class XmlDatasetReaderTest {
    @Test
    void datasetInfo() {
        DataSetReader reader = new XmlDatasetReader();
        for (Dataset dataset : Dataset.values()) {
            reader.setDataset(dataset);
        }
    }
}