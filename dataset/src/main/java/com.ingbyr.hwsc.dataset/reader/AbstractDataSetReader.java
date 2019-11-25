package com.ingbyr.hwsc.dataset.reader;

import com.ingbyr.hwsc.common.models.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.DocumentException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Slf4j
public abstract class AbstractDataSetReader implements DataSetReader {
    protected final String PREFIX_URL = "dataset//data//wsc";
    protected final String DATA_SET;
    protected final String DATA_SET_ID;

    protected Map<String, Thing> thingMap;
    protected Map<String, Service> serviceMap;
    protected Map<String, Param> paramMap;
    protected Map<String, Concept> conceptMap;

    // Left value is init p level and right value is goal set
    protected Pair<Set<Concept>, Set<Concept>> problem;
    protected Qos minOriginQos = new Qos(Double.MAX_VALUE);
    protected Qos maxOriginQos = new Qos(Double.MIN_VALUE);

    protected AbstractDataSetReader(String data_set, String data_set_id) {
        this.DATA_SET = data_set;
        this.DATA_SET_ID = data_set_id;


        this.thingMap = new HashMap<>();
        this.serviceMap = new HashMap<>();
        this.paramMap = new HashMap<>();
        this.conceptMap = new HashMap<>();
    }

    /**
     * Parse taxonomy file
     *
     * @return Concept map
     * @throws DocumentException
     */
    protected abstract Map<String, Concept> parseTaxonomyDocument() throws DocumentException;

    /**
     * Parse services file
     *
     * @return Service map
     * @throws DocumentException
     */
    protected abstract Map<String, Service> parseServicesDocument() throws DocumentException;

    /**
     * Parse problem or challenge file
     *
     * @return Problem
     * @throws DocumentException
     */
    protected abstract Pair<Set<Concept>, Set<Concept>> parseProblemDocument() throws DocumentException;

    @Override
    public void process() {
        try {
            parseTaxonomyDocument();
            parseServicesDocument();
            rescaleQos();
            parseProblemDocument();
            log.debug("Input set: {}", getInputSet());
            log.debug("Goal set: {}", getGoalSet());
            log.debug("Service map: {}", getServiceMap().size());
            log.debug("Concept map: {}", getConceptMap().size());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    protected void rescaleQos() {

    }

    protected void buildConceptIndex() {
        /**
         * build indexing for concept
         */
        for (String key : conceptMap.keySet()) {
            Concept concept = conceptMap.get(key);
            Concept varConcept = conceptMap.get(key);
            do {
                concept.addConceptToParentIndex(varConcept);//ancestor classes
                varConcept.addConceptToChildrenIndex(concept);//descendant classes
                if (varConcept.isRoot()) {
                    varConcept = null;
                } else {
                    varConcept = conceptMap.get(varConcept
                            .getDirectParentName());
                }

            } while (varConcept != null);
        }
    }

    protected void buildServiceIndex() {
        serviceMap.forEach((name, service) -> {
            for (Concept concept : service.getInputConceptSet()) {
                concept.addUsedByService(service);
            }

            for (Concept concept : service.getOutputConceptSet()) {
                concept.addProducedByService(service);
            }
        });
    }
}
