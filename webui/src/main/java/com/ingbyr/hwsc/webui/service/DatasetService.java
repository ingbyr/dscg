package com.ingbyr.hwsc.webui.service;

import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.webui.dao.DatasetDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DatasetService {

    private final DataSetReader xmlDatasetReader;

    private final DatasetDao datasetDao;

    private final RedisCommands redisCommands;


    @Autowired
    public DatasetService(DataSetReader xmlDatasetReader, DatasetDao datasetDao
            , RedisCommands redisCommands) {
        this.xmlDatasetReader = xmlDatasetReader;
        this.datasetDao = datasetDao;
        this.redisCommands = redisCommands;
    }

    public void resetDataset(Dataset dataset) {
        log.debug("Flush database and load {}", dataset);
        redisCommands.flushDb();
        xmlDatasetReader.setDataset(dataset);
        xmlDatasetReader.process();
        datasetDao.saveServiceMap(xmlDatasetReader.getServiceMap());
        datasetDao.saveConceptMap(xmlDatasetReader.getConceptMap());
    }
}
