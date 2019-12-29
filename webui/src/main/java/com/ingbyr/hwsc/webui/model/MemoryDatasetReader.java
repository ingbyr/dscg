package com.ingbyr.hwsc.webui.model;

import com.ingbyr.hwsc.common.AbstractDataSetReader;
import com.ingbyr.hwsc.common.DataSetReader;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;

@Setter
@Component
@NoArgsConstructor
public final class MemoryDatasetReader extends AbstractDataSetReader {

    public void cloneFrom(DataSetReader dataSetReader) {
        dataset = dataSetReader.getDataset();
        thingMap = new HashMap<>(dataSetReader.getThingMap());
        serviceMap = new HashMap<>(dataSetReader.getServiceMap());
        paramMap = new HashMap<>(dataSetReader.getParamMap());
        conceptMap = new HashMap<>(dataSetReader.getConceptMap());
        inputSet = new HashSet<>(dataSetReader.getInputSet());
        goalSet = new HashSet<>(dataSetReader.getGoalSet());
    }

}
