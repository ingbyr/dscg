package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;

import java.util.Map;
import java.util.Set;

public interface DataSetReader {

    void setDataset(Dataset dataset);

    void setDataset(String dataset);

    void process();

    Qos getMaxOriginQos();

    Qos getMinOriginQos();

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

    Set<Concept> getInputSet();

    Set<Concept> getGoalSet();
}
