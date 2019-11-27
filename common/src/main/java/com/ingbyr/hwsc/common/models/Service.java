package com.ingbyr.hwsc.common.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * The class represents a web service
 */
@Setter
@Getter
public class Service extends NamedObject {

    private Set<Param> inputParamSet;
    private Set<Param> outputParamSet;
    private Set<Concept> inputConceptSet;
    private Set<Concept> outputConceptSet;
    private Qos qos;
    private Qos originQos;
    private double cost;

    public Service(String name) {
        super(name);
        inputParamSet = new HashSet<>();
        outputParamSet = new HashSet<>();
        inputConceptSet = new HashSet<>();
        outputConceptSet = new HashSet<>();
        qos = new Qos();
        originQos = new Qos();
        cost = 0.0;
    }

    public void addInputParam(Param param) {
        this.inputParamSet.add(param);
    }

    public void addOutputParam(Param param) {
        this.outputParamSet.add(param);
    }

    public void addInputConcept(Concept concept) {
        this.inputConceptSet.add(concept);
    }

    public void addOutputConcept(Concept concept) {
        this.outputConceptSet.add(concept);
    }

}
