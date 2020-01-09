package com.ingbyr.dscg;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author ingbyr
 */
@Slf4j
@Getter
public class HeuristicInfo {

    int[] candidateStartTimes;

    Map<Integer, Set<Concept>> conceptsAtTime;

    Map<Concept, Integer> earliestTimeOfConcept;

    Map<String, Service> serviceMap;

    Map<String, Concept> conceptMap;

    int time;

    public boolean setup(DataSetReader dataSetReader) {

        conceptsAtTime = new HashMap<>();
        earliestTimeOfConcept = new HashMap<>();
        serviceMap = new HashMap<>();
        conceptMap = new HashMap<>();
        List<Integer> cst = new LinkedList<>();

        // Input set at time 0
        time = 0;
        conceptsAtTime.put(time, dataSetReader.getInputSet());
        for (Concept concept : dataSetReader.getInputSet()) {
            earliestTimeOfConcept.put(concept, time);
            conceptMap.put(concept.getName(), concept);
        }

        Set<Concept> state = new HashSet<>(dataSetReader.getInputSet());
        // Start from time 1
        time = 1;
        Set<Map.Entry<String, Service>> services = dataSetReader.getServiceMap().entrySet();
        while (!state.containsAll(dataSetReader.getGoalSet())) {
            boolean hasNewConcept = false;
            // Init
            conceptsAtTime.put(time, Sets.newHashSet(conceptsAtTime.get(time - 1)));
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
                            state.add(concept);
                            hasNewConcept = true;
                        }
                    }
                    if (hasNewConcept) {
                        serviceMap.put(service.getName(), service);
                    }
                }
            }

            if (hasNewConcept) {
                conceptsAtTime.put(time, new HashSet<>(state));
                cst.add(time);
            } else {
                return false;
            }
        }
        time--;
        candidateStartTimes = cst.stream().mapToInt(Integer::intValue).toArray();

        log.debug("Total time {}", time);
        log.debug("Candidate start times {}", Arrays.toString(candidateStartTimes));
        conceptsAtTime.forEach((time, concepts) -> {
            log.debug("Time {}, concepts size {} ", time, concepts.size());
        });
        log.debug("Total concepts {}, reachable concepts {}", dataSetReader.getConceptMap().size(), earliestTimeOfConcept.size());
        log.debug("Total services {}, reachable service {}", dataSetReader.getServiceMap().size(), serviceMap.size());
        log.debug("Reachable concepts include goal set");
        return true;
    }

    void update() {
        // TODO
    }
}
