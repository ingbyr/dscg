package com.ingbyr.hwsc.webui.service;

import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.webui.dao.DatasetDao;
import org.springframework.stereotype.Service;

@Service
public class DatasetService {

    private final DataSetReader xmlDatasetReader;

    private final DatasetDao datasetDao;

    public DatasetService(DataSetReader xmlDatasetReader, DatasetDao datasetDao) {
        this.xmlDatasetReader = xmlDatasetReader;
        this.datasetDao = datasetDao;
    }

    public void saveDatasetToDatabase(Dataset dataset) {
        xmlDatasetReader.setDataset(dataset);
        xmlDatasetReader.process();
        datasetDao.saveServiceMap(xmlDatasetReader.getServiceMap());
        datasetDao.saveConceptMap(xmlDatasetReader.getConceptMap());
    }
}
