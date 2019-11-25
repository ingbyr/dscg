package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.util.FileUtils;

public enum Dataset {

    wsc2008_01("//wsc2008//Testset01"),
    wsc2008_02("//wsc2008//Testset02"),
    wsc2008_03("//wsc2008//Testset03"),
    wsc2008_04("//wsc2008//Testset04"),
    wsc2008_05("//wsc2008//Testset05"),
    wsc2008_06("//wsc2008//Testset06"),
    wsc2008_07("//wsc2008//Testset07"),
    wsc2008_08("//wsc2008//Testset08"),
    wsc2009_01("//wsc2009//Testset01"),
    wsc2009_02("//wsc2009//Testset02"),
    wsc2009_03("//wsc2009//Testset03"),
    wsc2009_04("//wsc2009//Testset04"),
    wsc2009_05("//wsc2009//Testset05");

    private String path;

    private static final String DATA_PATH_PREFIX = FileUtils.PROJECT_DIR.resolve("data").normalize().toString();

    Dataset(String path) {
        this.path = path;
    }

    public String getPath() {
        return DATA_PATH_PREFIX + path;
    }
}
