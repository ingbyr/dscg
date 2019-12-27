package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import org.junit.jupiter.api.Test;

/**
 * @author ingbyr
 */
class ContextTest {

    @Test
    void build() {
        DataSetReader dataSetReader = new XMLDataSetReader(Dataset.wsc2009_01);
        Context context = new Context();
        context.setup(dataSetReader);
    }
}