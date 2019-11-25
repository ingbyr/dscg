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

    private final String TAXONOMY_URL;
    private final String SERVICES_URL;
    private final String PROBLEM_URL;

    public XMLDataSetReader(Dataset dataset) {
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
                        concept.addConceptToParentIndex(null);
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
        for (int type : Qos.types) {
            originQos.set(type, Double.parseDouble(serviceElement.attribute(Qos.names[type]).getText()));
        }

        // Normalize qos value
        service.setQos(QosUtils.normalize(originQos));

        // Single cost that not used in daex process
        service.setCost(QosUtils.toSimpleSingeQos(service));
    }

    @Override
    protected void rescaleQos() {
        // Init min qos and max qos
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            Qos serviceOriginQos = entry.getValue().getOriginQos();
            for (int type : Qos.types) {
                if (minOriginQos.get(type) > serviceOriginQos.get(type))
                    minOriginQos.set(type, serviceOriginQos.get(type));
                if (maxOriginQos.get(type) < serviceOriginQos.get(type))
                    maxOriginQos.set(type, serviceOriginQos.get(type));
            }
        }

        Qos maxMinQos = new Qos();
        // FIXME max-min must not equal 0
        for (int type : Qos.types) {
            maxMinQos.set(type, maxOriginQos.get(type) - minOriginQos.get(type));
        }
        log.debug("Min origin qos {}", minOriginQos);
        log.debug("Max origin qos {}", maxOriginQos);
        log.debug("Max - Min origin qos {}", maxMinQos);

        // rescale
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            Qos serviceOriginQos = entry.getValue().getOriginQos();
            Qos serviceQos = entry.getValue().getQos();
            for (int type : Qos.types) {
                serviceQos.set(type, (serviceOriginQos.get(type) - minOriginQos.get(type)) / maxMinQos.get(type));
            }
            log.trace("service origin qos {}", serviceOriginQos);
            log.trace("service qos {}", serviceQos);
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
