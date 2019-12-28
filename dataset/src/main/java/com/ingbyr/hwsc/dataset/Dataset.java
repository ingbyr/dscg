package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.WorkDir;
import lombok.Getter;

import java.nio.file.Path;

public enum Dataset {

    wsc2008_01("2008", "01"),
    wsc2008_02("2008", "02"),
    wsc2008_03("2008", "03"),
    wsc2008_04("2008", "04"),
    wsc2008_05("2008", "05"),
    wsc2008_06("2008", "06"),
    wsc2008_07("2008", "07"),
    wsc2008_08("2008", "08"),
    wsc2009_01("2009", "01"),
    wsc2009_02("2009", "02"),
    wsc2009_03("2009", "03"),
    wsc2009_04("2009", "04"),
    wsc2009_05("2009", "05");

    @Getter
    private final String datasetId1;

    @Getter
    private final String datasetId2;

    @Getter
    private Path path;

    Dataset(String datasetId1, String datasetId2) {
        this.datasetId1 = datasetId1;
        this.datasetId2 = datasetId2;
        this.path = WorkDir.WORK_DIR.resolve("data")
                .resolve("wsc" + datasetId1)
                .resolve("Testset" + datasetId2)
                .normalize();
    }
}
