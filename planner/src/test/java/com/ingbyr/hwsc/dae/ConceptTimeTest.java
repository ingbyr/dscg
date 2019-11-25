package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.reader.DataSetReader;
import com.ingbyr.hwsc.reader.XMLDataSetReader;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class ConceptTimeTest {

    @Test
    void build() {
        DataSetReader dataSetReader = new XMLDataSetReader("2009", "01");
        dataSetReader.process();
        ConceptTime conceptTime = new ConceptTime();
        conceptTime.build(dataSetReader);
    }
}