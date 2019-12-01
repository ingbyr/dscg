package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Service;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.Map;

@Slf4j
public abstract class LocalDatasetSetReader extends AbstractDataSetReader {
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
    protected abstract void parseProblemDocument() throws DocumentException;

    public void process() {
        try {
            parseTaxonomyDocument();
            parseServicesDocument();
            parseProblemDocument();
            log.debug("Input set: {}", getInputSet());
            log.debug("Goal set: {}", getGoalSet());
            log.debug("Service map: {}", getServiceMap().size());
            log.debug("Concept map: {}", getConceptMap().size());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO Use Localdataset reader
     * Set concept's parents and children
     */
    protected void buildConceptIndex() {
        for (String key : conceptMap.keySet()) {
            Concept concept = conceptMap.get(key);
            Concept varConcept = conceptMap.get(key);
            do {
                concept.addConceptToParentIndex(varConcept);//ancestor classes
                varConcept.addConceptToChildrenIndex(concept);//descendant classes
                if (varConcept.isRoot()) {
                    varConcept = null;
                } else {
                    varConcept = conceptMap.get(varConcept.getDirectParentName());
                }

            } while (varConcept != null);
        }
    }

    /**
     * Set used and produced by service cache
     */
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