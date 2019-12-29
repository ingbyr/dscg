package com.ingbyr.hwsc.common;


import java.util.Map;
import java.util.Set;

public interface DataSetReader {

    void setDataset(Dataset dataset);

    Dataset getDataset();

    Qos getMaxQos();

    Qos getMinQos();

    Qos getDistanceQos();

    /**
     * Get service map
     *
     * @return Service map
     */
    Map<String, Service> getServiceMap();

    /**
     * Get concept map
     *
     * @return Concept map
     */
    Map<String, Concept> getConceptMap();

    Map<String, Thing> getThingMap();

    Map<String, Param> getParamMap();

    Set<Concept> getInputSet();

    Set<Concept> getGoalSet();
}
