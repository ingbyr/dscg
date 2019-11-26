package com.ingbyr.hwsc.common.models;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents semantic concept in taxonomy document
 */
public class Concept extends NamedObject {

    private boolean root;
    private String directParentName;
    private Set<Concept> parentConceptsIndex;
    private Set<Concept> childrenConceptsIndex;
    private Set<Service> producedByServices;
    private Set<Service> usedByServices;
    private boolean goal;

    public Concept(String name) {
        super(name);
        this.root = false;
        this.parentConceptsIndex = new HashSet<>();
        this.childrenConceptsIndex = new HashSet<>();
        this.producedByServices = new HashSet<>();
        this.usedByServices = new HashSet<>();
        this.goal = false;
    }

    public String getDirectParentName() {
        return directParentName;
    }

    public void setDirectParentName(String directParentName) {
        this.directParentName = directParentName;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public void addConceptToParentIndex(Concept concept) {
        this.parentConceptsIndex.add(concept);
    }

    public void removeConceptFromParentIndex(Concept concept) {
        this.parentConceptsIndex.remove(concept);
    }

    public void addConceptToChildrenIndex(Concept concept) {
        this.childrenConceptsIndex.add(concept);
    }

    public void removeConceptFromChildrenIndex(Concept concept) {
        this.childrenConceptsIndex.remove(concept);
    }

    public Set<Concept> getParentConceptsIndex() {
        return parentConceptsIndex;
    }

    public Set<Concept> getChildrenConceptsIndex() {
        return childrenConceptsIndex;
    }

    public void addProducedByService(Service service) {
        this.producedByServices.add(service);
    }

    public Set<Service> getProducedByServices() {
        return producedByServices;
    }

    public void addUsedByService(Service service) {
        this.usedByServices.add(service);
    }

    public Set<Service> getUsedByServices() {
        return usedByServices;
    }

    public boolean isGoal() {
        return goal;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

}
