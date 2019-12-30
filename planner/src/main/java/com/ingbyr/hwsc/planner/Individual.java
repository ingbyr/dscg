package com.ingbyr.hwsc.planner;

import com.google.common.collect.Lists;
import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Qos;
import com.ingbyr.hwsc.common.QosUtils;
import com.ingbyr.hwsc.common.Service;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * S = (S(i)) i in [0,n+1] (with S(0) = I and S(n+1) = G)
 *
 * @author ingbyr
 */
@EqualsAndHashCode
@Getter
@Setter
@Slf4j
@ToString
public class Individual implements Comparable<Individual> {

    static int globalId = 0;

    /**
     * State list
     */
    List<State> states = Lists.newArrayList();

    /**
     * Id
     */
    @EqualsAndHashCode.Exclude
    private int id;

    /**
     * Services that can be executed in this states
     */
    @EqualsAndHashCode.Exclude
    private List<Service> services = null;

    /**
     * Qos from services
     */
    @EqualsAndHashCode.Exclude
    private Qos qos = null;

    /**
     * State index that can be reached by executing the services
     */
    @EqualsAndHashCode.Exclude
    int lastReachedStateIndex = 0;

    /**
     * If can reach last state (goal set), feasible is true. Otherwise is false
     */
    @EqualsAndHashCode.Exclude
    boolean isFeasible = false;

    /**
     * When isFeasible equals false, it is search cost from internal planner;
     * When isFeasible equals true, it is a measure for the "loss in quality"
     * if this individual is removed from the current population;
     */
    @EqualsAndHashCode.Exclude
    double fitness = 0.0;

    public Individual() {
        this.id = globalId++;
    }

    public void offsetFitness(double offset) {
        fitness += offset;
    }

    public Individual copy() {
        Individual newInd = new Individual();
        List<State> newStates = new ArrayList<>(this.states.size());
        for (State state : this.states) {
            newStates.add(state.copy());
        }
        newInd.states = newStates;
        return newInd;
    }

    void addState(State state) {
        states.add(state);
    }

    void addState(int index, State state) {
        states.add(index, state);
    }

    State getState(int index) {
        return states.get(index);
    }

    State removeState(int index) {
        return states.remove(index);
    }

    public int getStateSize() {
        return states.size();
    }

    public Set<Concept> getInputSet() {
        return states.get(0).concepts;
    }

    public Set<Concept> getGoalSet() {
        return states.get(states.size() - 1).concepts;
    }

    /**
     * Set services and qos of services
     *
     * @param services Services
     */
    public void setServices(List<Service> services) {
        if (services == null)
            return;

        this.services = services;
        this.qos = QosUtils.mergeQos(services);
        log.trace("{} scaled {}", id, qos);
    }

    /**
     * The comparison between any two individuals assumes
     * that a feasible individual is always preferred to an infeasible
     * one, regardless of any fitness value. Otherwise use fitness to compare.
     *
     * @param another Another individual
     * @return Compare result
     */
    @Override
    public int compareTo(Individual another) {
        if (this.isFeasible && (!another.isFeasible)) return 1;
        else if ((!this.isFeasible) && another.isFeasible) return -1;
        else return Double.compare(this.fitness, another.fitness);
    }

    public String toSimpleInfo() {
        return "Ind{id=" + id +
                ", q=" + qos +
                ", f=" + isFeasible +
                ", F=" + fitness +
                '}';
    }

    public String toStatesStr() {
        return states.toString();
    }
}
