package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import com.ingbyr.hwsc.planner.ConceptTime;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class ConceptTimeTest {

    @Test
    void build() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        dataSetReader.process();
        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);
    }
}