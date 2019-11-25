package com.ingbyr.hwsc.dataset.reader;

import com.ingbyr.hwsc.common.models.*;
import com.ingbyr.hwsc.common.util.FileUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Slf4j
public abstract class AbstractDataSetReader implements DataSetReader {

    protected Map<String, Thing> thingMap = new HashMap<>();
    protected Map<String, Service> serviceMap = new HashMap<>();
    protected Map<String, Param> paramMap = new HashMap<>();
    protected Map<String, Concept> conceptMap = new HashMap<>();

    // Left value is init p level and right value is goal set
    protected Pair<Set<Concept>, Set<Concept>> problem;
    protected Qos minOriginQos = new Qos(Double.MAX_VALUE);
    protected Qos maxOriginQos = new Qos(Double.MIN_VALUE);

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

    protected File loadFile(String filePath) {
        File file = new File(filePath);
        log.debug("Load file {}", file.getAbsolutePath());
        return file;
    }
}
