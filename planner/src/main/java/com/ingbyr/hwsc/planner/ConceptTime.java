package com.ingbyr.hwsc.planner;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.dataset.DataSetReader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ingbyr
 */
@Slf4j
public class ConceptTime {

    public int[] candidateStartTimes;

    public Map<Integer, Set<Concept>> conceptsAtTime = new HashMap<>();

    Map<Concept, Integer> earliestTimeOfConcept = new HashMap<>();

    int time = 0;

    void build(DataSetReader dataSetReader) {
        // Input set
        conceptsAtTime.put(time, Sets.newHashSet(dataSetReader.getInputSet()));
        for (Concept concept : dataSetReader.getInputSet()) {
            earliestTimeOfConcept.put(concept, time);
        }

        time++;

        Set<Service> services = Sets.newHashSet(dataSetReader.getServiceMap().values());

        while (true) {
            boolean hasNewConcept = false;
            // Init
            conceptsAtTime.put(time, Sets.newHashSet(conceptsAtTime.get(time - 1)));
            Set<Concept> currentConcept = earliestTimeOfConcept.keySet();
            for (Service service : services) {
                // If service can be executed in this time
                if (currentConcept.containsAll(service.getInputConceptSet())) {
                    // Build output index
                    for (Concept concept : service.getOutputConceptSet()) {
                        // If concept never appears before
                        if (!earliestTimeOfConcept.containsKey(concept)) {
                            earliestTimeOfConcept.put(concept, time);
                            conceptsAtTime.get(time).add(concept);
                            hasNewConcept = true;
                        }
                    }
                }
            }

            if (!hasNewConcept) {
                conceptsAtTime.remove(time--);
                break;
            } else {
                time++;
            }
        }

        candidateStartTimes = conceptsAtTime.keySet().stream()
                .mapToInt(Integer::intValue)
                .filter(i -> i != 0) // Skip input time
                .toArray();

        log.debug("Total time {}", time);
        log.debug("Candidate start times {}", Arrays.toString(candidateStartTimes));
        conceptsAtTime.forEach((time, concepts) -> {
            log.debug("Time {}, concepts size {} ", time, concepts.size());
        });
        log.debug("Total concepts {}, reachable concepts {}", dataSetReader.getConceptMap().size(), earliestTimeOfConcept.size());
        log.debug("Reachable concepts include goal set: {}", conceptsAtTime.get(time).containsAll(dataSetReader.getGoalSet()));
    }

}