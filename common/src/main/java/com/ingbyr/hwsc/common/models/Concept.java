package com.ingbyr.hwsc.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents semantic concept in taxonomy document
 */
@Getter
@Setter
public class Concept extends NamedObject {

    private boolean root = false;
    private boolean goal = false;
    private String directParentName;
    private Set<Concept> parentConcepts = new HashSet<>();
    private Set<Service> producedByServices = new HashSet<>();
    private Set<Service> usedByServices = new HashSet<>();

    public Concept(String name) {
        super(name);
    }

    public void addParentConcept(Concept concept) {
        this.parentConcepts.add(concept);
    }

    public void addProducedByService(Service service) {
        this.producedByServices.add(service);
    }

    public void addUsedByService(Service service) {
        this.usedByServices.add(service);
    }
}
