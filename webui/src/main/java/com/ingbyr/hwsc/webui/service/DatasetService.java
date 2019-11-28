package com.ingbyr.hwsc.webui.service;

import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.webui.model.MemoryDatasetReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DatasetService {

    private final DataSetReader xmlDatasetReader;

    private final MemoryDatasetReader memoryDatasetReader;


    @Autowired
    public DatasetService(DataSetReader xmlDatasetReader,
                          MemoryDatasetReader memoryDatasetReader) {
        this.xmlDatasetReader = xmlDatasetReader;
        this.memoryDatasetReader = memoryDatasetReader;
    }

    public void resetDataset(Dataset dataset) {
        log.debug("Reload dataset from {}", dataset);
        xmlDatasetReader.setDataset(dataset);
        memoryDatasetReader.cloneFrom(xmlDatasetReader);
    }

    public boolean needLoadDataset(Dataset dataset) {
        return memoryDatasetReader == null
                || memoryDatasetReader.getDataset() == null
                || !(memoryDatasetReader.getDataset() == dataset);
    }
}
