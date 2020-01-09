package com.ingbyr.dscg;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author ingbyr
 */
@Slf4j
@Getter
public class HeuristicInfo {

    int[] candidateStartTimes;

    List<Set<Concept>> conceptLevel;

    Map<Concept, Integer> earliestTimeOfConcept;

    Map<String, Service> serviceMap;

    Map<String, Concept> conceptMap;

    int time = 0;

    public boolean setup(DataSetReader dataSetReader) {

        conceptLevel = new ArrayList<>();
        earliestTimeOfConcept = new HashMap<>();
        serviceMap = new HashMap<>();
        conceptMap = new HashMap<>();

        // Input set at time 0
        time = 0;
        conceptLevel.add(new HashSet<>(dataSetReader.getInputSet()));
        for (Concept concept : dataSetReader.getInputSet()) {
            earliestTimeOfConcept.put(concept, time);
            conceptMap.put(concept.getName(), concept);
        }


        // Start from time 1
        time = 1;
        Set<Map.Entry<String, Service>> services = dataSetReader.getServiceMap().entrySet();
        while (true) {

            // Init
            Set<Concept> state = new HashSet<>(conceptLevel.get(time - 1));
            Set<Concept> newConceptSet = new HashSet<>();

            for (Map.Entry<String, Service> entry : services) {
                Service service = entry.getValue();
                // If service can be executed in this time
                if (state.containsAll(service.getInputConceptSet())) {
                    // Build output index
                    for (Concept concept : service.getOutputConceptSet()) {
                        // If concept never appears before
                        if (!earliestTimeOfConcept.containsKey(concept)) {
                            earliestTimeOfConcept.put(concept, time);
                            conceptMap.put(concept.getName(), concept);
                            newConceptSet.add(concept);
                        }
                    }
                    serviceMap.put(service.getName(), service);
                }
            }

            state.addAll(newConceptSet);

            conceptLevel.add(state);
            time++;

            if (state.containsAll(dataSetReader.getGoalSet())) {
                conceptLevel.add(new HashSet<>(state));
                break;
            }
        }

        candidateStartTimes = IntStream.rangeClosed(0, time).toArray();

        log.debug("Total time {}", time);
        log.debug("Candidate start times {}", Arrays.toString(candidateStartTimes));
        for (int i = 0; i < conceptLevel.size(); i++) {
            log.debug("Time {}, concepts size {} ", i, conceptLevel.get(i).size());
        }
        log.debug("Total concepts {}, reachable concepts {}", dataSetReader.getConceptMap().size(), earliestTimeOfConcept.size());
        log.debug("Total services {}, reachable service {}", dataSetReader.getServiceMap().size(), serviceMap.size());
        log.debug("Reachable concepts include goal set");
        return true;
    }
}
