package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.*;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Getter
@Slf4j
@ToString
public abstract class AbstractDataSetReader implements DataSetReader {

    protected Dataset dataset;
    protected Map<String, Thing> thingMap;
    protected Map<String, Service> serviceMap;
    protected Map<String, Param> paramMap;
    protected Map<String, Concept> conceptMap;
    protected Set<Concept> inputSet;
    protected Set<Concept> goalSet;
    protected QoS minQoS;
    protected QoS maxQoS;
    protected QoS distanceQoS;

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }
}
