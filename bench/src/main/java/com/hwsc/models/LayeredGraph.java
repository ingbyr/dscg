package com.hwsc.models;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Service;

import java.util.*;

public class LayeredGraph {

    private DataSetReader reader;

    private List<Set<Concept>> conceptLayer = new ArrayList<>();

    private List<Set<Service>> serviceLayer = new ArrayList<>();

    private boolean reachGoalSet() {
        Set<Concept> goalSet = new HashSet<>(reader.getGoalSet());
        for (Set<Concept> conceptLayerSet : conceptLayer) {
            goalSet.removeAll(conceptLayerSet);
        }
        return goalSet.isEmpty();
    }

    public void setup() {
        int level = 0;
        conceptLayer.add(reader.getInputSet());

        Set<Service> remainingServices = new HashSet<>(reader.getServiceMap().values());

        while (!reachGoalSet()) {
            Set<Service> serviceLayerSet = new HashSet<>();
            serviceLayer.add(serviceLayerSet);
            Set<Concept> conceptLayerSet = new HashSet<>();
            conceptLayer.add(conceptLayerSet);

            Iterator<Service> itr = remainingServices.iterator();
            while (itr.hasNext()) {
                Service service = itr.next();
                // If service can be executed in this level
                if (conceptLayer.get(level).containsAll(service.getInputConceptSet())) {

                    serviceLayerSet.add(service);
                    itr.remove();
                }
            }
        }

    }
}
