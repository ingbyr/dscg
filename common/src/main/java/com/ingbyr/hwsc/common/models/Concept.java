package com.ingbyr.hwsc.common.models;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents semantic concept in taxonomy document
 */
public class Concept extends NamedObject {

    private boolean root;
    private String directParentName;
//    private Set<Service> servicesIndex;
//    private Set<Service> originServiceSet;
    private Set<Concept> parentConceptsIndex;
    private Set<Concept> childrenConceptsIndex;
    private Set<Service> producedByServices;
    private Set<Service> usedByServices;
    private boolean rin;
    private boolean goal;

    public Concept(String name) {
        super(name);
        this.root = false;
//        this.servicesIndex = new HashSet<>();
//        this.originServiceSet = new HashSet<>();
        this.parentConceptsIndex = new HashSet<>();
        this.childrenConceptsIndex = new HashSet<>();
        this.producedByServices = new HashSet<>();
        this.usedByServices = new HashSet<>();
        this.rin = false;
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

//    public Set<Service> getServicesIndex() {
//        return servicesIndex;
//    }
//
//    public void addServiceToIndex(Service service) {
//        this.servicesIndex.add(service);
//    }

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

//    public void addServiceToOrigin(Service service) {
//        this.originServiceSet.add(service);
//    }
//
//    public Set<Service> getOriginServiceSet() {
//        return originServiceSet;
//    }
//
//    public void resetServiceIndex() {
//        this.servicesIndex = new HashSet<Service>();
//        this.originServiceSet = new HashSet<Service>();
//    }

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

    public boolean isRin() {
        return rin;
    }

    public void setRin(boolean rin) {
        this.rin = rin;
    }

    public boolean isGoal() {
        return goal;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

}
