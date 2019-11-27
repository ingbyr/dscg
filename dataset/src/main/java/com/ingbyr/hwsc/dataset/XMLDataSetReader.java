package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.models.*;
import com.ingbyr.hwsc.dataset.util.QosUtils;
import com.ingbyr.hwsc.dataset.util.XMLFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.util.*;

/**
 * @author ing
 */
@Slf4j
public class XMLDataSetReader extends AbstractDataSetReader implements DataSetReader {

    // taxonomy xml constants
    private static final String NAME = "name";
    private static final String CONCEPT = "concept";
    private static final String INSTANCE = "instance";

    // services xml constants
    private static final String INPUTS = "inputs";
    private static final String OUTPUTS = "outputs";

    // problem xml constants
    private static final String TASK = "task";
    private static final String PROVIDED = "provided";
    private static final String WANTED = "wanted";


    private String TAXONOMY_URL;
    private String SERVICES_URL;
    private String PROBLEM_URL;

    public XMLDataSetReader() {
    }

    public XMLDataSetReader(Dataset dataset) {
        setDataset(dataset);
    }

    @Override
    public void setDataset(String dataset) {
        setDataset(Dataset.valueOf(dataset));
    }

    @Override
    public void setDataset(Dataset dataset) {
        this.TAXONOMY_URL = dataset.getPath() + File.separator + "taxonomy.xml";
        this.SERVICES_URL = dataset.getPath() + File.separator + "services-qos.xml";
        this.PROBLEM_URL = dataset.getPath() + File.separator + "problem.xml";
    }

    @Override
    public Map<String, Concept> parseTaxonomyDocument() throws DocumentException {

        Queue<Element> elementsQueue = new LinkedList<>();
        elementsQueue.offer(XMLFileUtils.loadRootElement(TAXONOMY_URL));

        // BFS
        while (!elementsQueue.isEmpty()) {
            Element element = elementsQueue.poll();
            XMLFileUtils.walkOnChild(element, ele -> {
                elementsQueue.offer(ele);
                if (ele.getName().equals(CONCEPT)) { // concept
                    Concept concept = new Concept(ele.attribute(NAME).getText());
                    // set the parent of concept
                    Element parentElement = ele.getParent();
                    if (parentElement.getName().equals(CONCEPT)) {
                        String conceptParentName = parentElement.attribute(NAME).getText();
                        concept.setDirectParentName(conceptParentName);
                    } else {
                        concept.setRoot(true);
                    }
                    // store concept
                    conceptMap.put(concept.getName(), concept);
                } else if (ele.getName().equals(INSTANCE)) { // instance
                    Thing thing = new Thing(ele.attribute(NAME).getText());
                    thing.setType(ele.getParent().attribute(NAME).getText());
                    // cache thing
                    thingMap.put(thing.getName(), thing);
                }
            });
        }
        log.debug("Build concept index");
        buildConceptIndex();
        return getConceptMap();
    }

    /**
     * Parse qos of service
     *
     * @param service
     * @param serviceElement
     */
    private void parseServiceQos(Service service, Element serviceElement) {
        Qos originQos = service.getOriginQos();
        for (int type : Qos.TYPES) {
            originQos.set(type, Double.parseDouble(serviceElement.attribute(Qos.NAMES[type]).getText()));
        }

        // Standard qos value
        service.setQos(QosUtils.flip(originQos));

        log.trace("{} origin {}", service, service.getOriginQos());

        // Single cost that not used in plan process
        service.setCost(QosUtils.toSimpleCost(service));
    }

    /**
     * Rescale qos
     */
    private void rescaleQos() {
        // Init min qos and max qos
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            Qos standardQos = entry.getValue().getQos();
            for (int type : Qos.TYPES) {
                if (minQos.get(type) > standardQos.get(type))
                    minQos.set(type, standardQos.get(type));
                if (maxQos.get(type) < standardQos.get(type))
                    maxQos.set(type, standardQos.get(type));
            }
        }
        // Calculate distance qos
        for (int type : Qos.TYPES) {
            distanceQos.set(type, maxQos.get(type) - minQos.get(type));
        }
        log.debug("Min {}", minQos);
        log.debug("Max {}", maxQos);
        log.debug("Distance {}", distanceQos);

        // Rescale qos
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            Qos standardQos = entry.getValue().getQos();
            Qos rescaledQos = new Qos();
            for (int type : Qos.TYPES) {
                double distance = distanceQos.get(type);
                // Avoid distance equals 0
                if (distance == 0.0) {
                    distance = 1.0;
                }
                rescaledQos.set(type, (standardQos.get(type) - minQos.get(type)) / distance);
            }
            entry.getValue().setQos(rescaledQos);
            log.trace("{} rescale {}", entry.getKey(), standardQos);
        }

    }

    /**
     * Parse services params
     *
     * @param service       Service
     * @param paramsElement Params element
     */
    private void parseServiceParams(Service service, Element paramsElement) {
        XMLFileUtils.walkOnChild(paramsElement, paramElement -> {
            String instanceName = paramElement.attribute(NAME).getText();
            Param param = new Param(instanceName);
            Thing thing = thingMap.get(instanceName);
            param.setThing(thing);
            paramMap.put(param.getName(), param);
            String paramsType = paramsElement.getName();
            switch (paramsType) {
                case INPUTS:
                    service.addInputParam(param);
                    service.addInputConcept(conceptMap.get(thing.getType()));
                    break;
                case OUTPUTS:
                    service.addOutputParam(param);
                    conceptMap.get(thing.getType()).getParentConceptsIndex().stream().filter(Objects::nonNull).forEach(service::addOutputConcept);
                    break;
                default:
                    log.error("Can not parse {}", paramElement);
            }
        });
    }

    /**
     * Parse services file with qos value. Notice that there are no params
     *
     * @return Service map
     * @throws DocumentException
     */
    @Override
    public Map<String, Service> parseServicesDocument() throws DocumentException {
        Element servicesRoot = XMLFileUtils.loadRootElement(SERVICES_URL);
        XMLFileUtils.walkOnChild(servicesRoot, serviceElement -> {
            Service service = new Service(serviceElement.attribute(NAME).getText());
            parseServiceQos(service, serviceElement);
            parseServiceParams(service, serviceElement.element(INPUTS));
            parseServiceParams(service, serviceElement.element(OUTPUTS));
            serviceMap.put(service.getName(), service);
        });
        buildServiceIndex();
        rescaleQos();
        return serviceMap;
    }

    private Set<Concept> parseInitialParas(Element instancesElement) {
        Set<Concept> res = new HashSet<>();
        String paramType = instancesElement.getName();

        XMLFileUtils.walkOnChild(instancesElement, instanceElement -> {
            String instanceName = instanceElement.attribute(NAME).getText();
            Param param = new Param(instanceName); // FIXME no param in problem.xml
            Thing thing = thingMap.get(instanceName);
            param.setThing(thing);
            paramMap.put(param.getName(), param);
            switch (paramType) {
                case PROVIDED:
                    res.addAll(conceptMap.get(thing.getType()).getParentConceptsIndex());
                    break;
                case WANTED:
                    res.add(conceptMap.get(thing.getType()));
                    break;
            }
        });
        return res;
    }

    @Override
    public Pair<Set<Concept>, Set<Concept>> parseProblemDocument() throws DocumentException {
        Element problemRoot = XMLFileUtils.loadRootElement(PROBLEM_URL);
        Element taskRoot = problemRoot.element(TASK);
        Set<Concept> initPLevel = parseInitialParas(taskRoot.element(PROVIDED));
        Set<Concept> goalSet = parseInitialParas(taskRoot.element(WANTED));
        problem = new ImmutablePair<>(initPLevel, goalSet);
        return problem;
    }

    @Override
    public Set<Concept> getInputSet() {
        return problem.getLeft();
    }

    @Override
    public Set<Concept> getGoalSet() {
        return problem.getRight();
    }
}
