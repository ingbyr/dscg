package com.ingbyr.hwsc.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.*;

@Getter
@Slf4j
public class WsdlDatasetReader extends LocalDatasetSetReader {

    private String TAXONOMY_URL;
    private String SERVICES_URL;
    private String PROBLEM_URL;

    public WsdlDatasetReader() {
    }

    public WsdlDatasetReader(Dataset dataset) {
        setDataset(dataset);
    }


    @Override
    public void setDataset(Dataset dataset) {
        super.setDataset(dataset);
        this.TAXONOMY_URL = dataset.getPath() + "//Taxonomy.owl";
        this.SERVICES_URL = dataset.getPath() + "//Services.wsdl";
        this.PROBLEM_URL = dataset.getPath() + "//Challenge.wsdl";
    }

    @Override
    public Map<String, Concept> parseTaxonomyDocument() throws DocumentException {
        conceptMap = new HashMap<>();
        thingMap = new HashMap<>();

        Element taxonomyRoot = XMLFileUtils.loadRootElement(loadFile(TAXONOMY_URL));

        /**
         * loop through semantic elements to check taxonomy
         */
        for (Iterator i = taxonomyRoot.elementIterator(); i.hasNext(); ) {
            Element el = (Element) i.next();
            if (el.getName().equals("Class")) {
                Concept concept = new Concept(el.attribute("ID").getText());// Get the concept name(ID)
                List<Element> list = el.elements();
                if (list.size() != 0) {
                    for (Element dummy : list) {
                        concept.setDirectParentName(dummy
                                .attribute("resource").getText()
                                .replaceAll("#", ""));// Get the direct parent class

                    }
                } else {
                    concept.setRoot(true);
                }
                conceptMap.put(concept.getName(), concept);

            } else if (el.getName().equals("Thing")) {
                Thing thing = new Thing(el.attribute("ID").getText());

                thing.setType(el.element("type").attribute("resource")
                        .getText().replaceAll("#", ""));// type = parent class
                thingMap.put(thing.getName(), thing);
            }
        }

        buildConceptIndex();
        return getConceptMap();
    }

    @Override
    public Map<String, Service> parseServicesDocument() throws DocumentException {
        serviceMap = new HashMap<>();
        paramMap = new HashMap<>();

        Element servicesRoot = XMLFileUtils.loadRootElement(loadFile(SERVICES_URL));
        Element semRoot = servicesRoot.element("semExtension");

        /**
         * loop through semantic elements
         */
        Service service = null;

        for (Iterator i = semRoot.elementIterator(); i.hasNext(); ) {
            Element semMsgExtEl = (Element) i.next();
            if (semMsgExtEl.getName().equals("semMessageExt")) {
                boolean isRequestParam;
                if (semMsgExtEl.attribute("id").getText().contains(
                        "RequestMessage")) {
                    service = new Service(semMsgExtEl.attribute("id").getText()
                            .replaceAll("RequestMessage", ""));
                    isRequestParam = true;
                } else {
                    isRequestParam = false;
                }

                for (Iterator j = semMsgExtEl.elementIterator(); j.hasNext(); ) {
                    Element semExtEl = (Element) j.next();
                    if (semExtEl.getName().equals("semExt")) {
                        Param param = new Param(semExtEl.attribute("id")
                                .getText());
                        Thing thing = thingMap.get(semExtEl.element(
                                "ontologyRef").getText().replaceAll(
                                "http://www.ws-challenge.org/wsc08.owl#", ""));

                        param.setThing(thing);
                        paramMap.put(param.getName(), param);
                        if (isRequestParam) {
                            service.addInputParam(param);
                            service.addInputConcept(conceptMap.get(thing.getType()));
                        } else {
                            service.addOutputParam(param);
                            for (Concept c : conceptMap.get(thing.getType()).getParentConcepts()) {
                                service.addOutputConcept(c);//outputs indexing
                            }
                        }
                    }
                }
                if (semMsgExtEl.attribute("id").getText().contains(
                        "ResponseMessage")) {
                    serviceMap.put(service.getName(), service);
                }

            }
        }
        return getServiceMap();
    }

    @Override
    public void parseProblemDocument() throws DocumentException {

        inputSet = new HashSet<>();
        goalSet = new HashSet<>();

        Element servicesRoot = XMLFileUtils.loadRootElement(loadFile(PROBLEM_URL));
        Element semRoot = servicesRoot.element("semExtension");

        for (Iterator i = semRoot.elementIterator(); i.hasNext(); ) {
            Element semMsgExtEl = (Element) i.next();
            if (semMsgExtEl.getName().equals("semMessageExt")) {
                boolean isRequestParam;
                if (semMsgExtEl.attribute("id").getText().contains(
                        "RequestMessage")) {
                    isRequestParam = true;
                } else {
                    isRequestParam = false;
                }

                for (Iterator j = semMsgExtEl.elementIterator(); j.hasNext(); ) {
                    Element semExtEl = (Element) j.next();
                    if (semExtEl.getName().equals("semExt")) {
                        Param param = new Param(semExtEl.attribute("id")
                                .getText());
                        Thing thing = thingMap.get(semExtEl.element(
                                "ontologyRef").getText().replaceAll(
                                "http://www.ws-challenge.org/wsc08.owl#", ""));

                        param.setThing(thing);
                        paramMap.put(param.getName(), param);
                        if (isRequestParam) {
                            inputSet.addAll(conceptMap.get(thing.getType()).getParentConcepts());
                        } else {
                            goalSet.add(conceptMap.get(thing.getType()));
                        }
                    }
                }
            }
        }
    }
}
