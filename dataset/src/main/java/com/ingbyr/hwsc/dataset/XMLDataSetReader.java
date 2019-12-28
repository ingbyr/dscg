package com.ingbyr.hwsc.dataset;

import com.ingbyr.hwsc.common.*;
import com.ingbyr.hwsc.dataset.util.XMLFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.util.*;

/**
 * @author ing
 */
@Slf4j
public class XMLDataSetReader extends LocalDatasetSetReader {

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
    public void setDataset(Dataset dataset) {
        super.setDataset(dataset);
        this.TAXONOMY_URL = dataset.getPath() + File.separator + "taxonomy.xml";
        this.SERVICES_URL = dataset.getPath() + File.separator + "services-qos.xml";
        this.PROBLEM_URL = dataset.getPath() + File.separator + "problem.xml";
        process();
    }

    @Override
    public Map<String, Concept> parseTaxonomyDocument() throws DocumentException {

        conceptMap = new HashMap<>();
        thingMap = new HashMap<>();

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
        QoS originQoS = new QoS();
        for (int type : QoS.TYPES) {
            originQoS.set(type, Double.parseDouble(serviceElement.attribute(QoS.NAMES[type]).getText()));
        }
        service.setOriginQoS(originQoS);
        log.trace("{} origin {}", service, service.getOriginQoS());
    }

    /**
     * Rescale qos
     */
    private void rescaleQos() {

        minQoS = new QoS(Double.MAX_VALUE);
        maxQoS = new QoS(Double.MIN_VALUE);
        distanceQoS = new QoS();

        // Init min qos and max qos
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            QoS qos = entry.getValue().getOriginQoS();
            for (int type : QoS.TYPES) {
                if (minQoS.get(type) > qos.get(type))
                    minQoS.set(type, qos.get(type));
                if (maxQoS.get(type) < qos.get(type))
                    maxQoS.set(type, qos.get(type));
            }
        }

        // Calculate distance qos
        for (int type : QoS.TYPES) {
            distanceQoS.set(type, maxQoS.get(type) - minQoS.get(type));
        }
        log.debug("Min {}", minQoS);
        log.debug("Max {}", maxQoS);
        log.debug("Distance {}", distanceQoS);

        // Rescale qos
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            Service service = entry.getValue();
            QoS qos = new QoS();
            for (int type : QoS.TYPES) {
                double distance = distanceQoS.get(type);
                // Avoid distance equals 0
                if (distance == 0.0) {
                    distance = 1.0;
                }
                qos.set(type, (service.getOriginQoS().get(type) - minQoS.get(type)) / distance);
            }
            service.setQos(qos);
            service.setCost(QosUtils.sumQosToCost(qos));
            log.trace("{} origin {}", service, service.getOriginQoS().getValues());
            log.trace("{} rescale {}", service, service.getQos().getValues());
        }

    }

    /**
     * Parse services params
     *
     * @param service       Service
     * @param paramsElement Params element
     */
    private void parseServiceParams(Service service, Element paramsElement) {
        paramMap = new HashMap<>();

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
                    conceptMap.get(thing.getType()).getParentConcepts().stream().filter(Objects::nonNull).forEach(service::addOutputConcept);
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
        serviceMap = new HashMap<>();
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
                    res.addAll(conceptMap.get(thing.getType()).getParentConcepts());
                    break;
                case WANTED:
                    res.add(conceptMap.get(thing.getType()));
                    break;
            }
        });
        return res;
    }

    @Override
    public void parseProblemDocument() throws DocumentException {
        inputSet = new HashSet<>();
        goalSet = new HashSet<>();

        Element problemRoot = XMLFileUtils.loadRootElement(PROBLEM_URL);
        Element taskRoot = problemRoot.element(TASK);
        inputSet = parseInitialParas(taskRoot.element(PROVIDED));
        goalSet = parseInitialParas(taskRoot.element(WANTED));
    }
}
