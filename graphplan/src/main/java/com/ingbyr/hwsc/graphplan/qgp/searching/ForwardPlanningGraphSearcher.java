package com.ingbyr.hwsc.graphplan.qgp.searching;

import com.ingbyr.hwsc.graphplan.qgp.models.Delta;
import com.ingbyr.hwsc.graphplan.qgp.models.PlanningGraph;
import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Param;
import com.ingbyr.hwsc.common.Service;
import lombok.Getter;

import java.util.*;
import java.util.Map.Entry;

public class ForwardPlanningGraphSearcher implements PlanningGraphSearcher {
    private List<String> servicesConsumed;
    private Set<Concept> goalSet;
    private PlanningGraph planningGraph;
    private Delta delta;
    private Map<String, Concept> conceptMap;
    private Map<String, Service> serviceMap;

    @Getter
    private long composeTime = 0;

    @Getter
    private long backTrackingTime = 0L;

    public ForwardPlanningGraphSearcher(PlanningGraph planningGraph, Map<String, Concept> conceptMap, Map<String, Service> serviceMap) {
        this.planningGraph = planningGraph;
        this.conceptMap = conceptMap;
        this.serviceMap = serviceMap;
    }

    @Override
    public PlanningGraph search() {
        doForWardSearch();
        return planningGraph;
    }

    private void doForWardSearch() {
        // init
        Vector<Set<Concept>> inputLevels = planningGraph.getPLevels();
        Set<Concept> inputSet = inputLevels.get(0);
        delta = new Delta(inputSet);
        servicesConsumed = new LinkedList<>();
        goalSet = planningGraph.getGoalSet();
        Map<String, Service> tempServices = new HashMap<>(serviceMap);
        LinkedHashSet<Concept> set = new LinkedHashSet<>(inputSet);
        planningGraph.propLevels.add(set);
        long start = System.currentTimeMillis();

        // compose
        compose(inputSet, tempServices, goalSet);

        // result
        planningGraph.delta = delta;
        composeTime = System.currentTimeMillis() - start;
    }

    /**
     * Expand the planning graph
     *
     * @param inputSet
     * @param tempServices
     * @param goalSet
     * @return
     */
    private boolean compose(Set<Concept> inputSet, Map<String, Service> tempServices, Set<Concept> goalSet) {
        if (compareSets(inputSet, goalSet)) {
            return true;
        }
        Set<Service> servicesToBeTried = getPossibleWSToBeConsumed(inputSet, tempServices);

        if (expandGraph(servicesToBeTried, tempServices, inputSet, servicesConsumed, new HashSet<>(), true)) {
            LinkedHashSet<Service> levels = new LinkedHashSet<>();
            for (String str : servicesConsumed) {
                Service svc = tempServices.remove(str);
                if (svc != null) {
                    levels.add(svc);
                    // update delta for A* alg
                    delta.update(svc);
                }
            }
            servicesConsumed = new LinkedList<>();
            if (levels.size() != 0) {
                planningGraph.actionLevels.add(levels);
                LinkedHashSet<Concept> set = new LinkedHashSet<>(inputSet);
                planningGraph.propLevels.add(set);
            }

            if (compose(inputSet, tempServices, goalSet)) {
                return true;
            } else {
                if (servicesConsumed.size() == tempServices.size()) {
                    System.out.println("No other services remaining to process the inputs.");
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Check if the input contains output
     *
     * @param inputSet Input
     * @param goalSet  output
     * @return if the input contains output return true
     */
    private boolean compareSets(Set<Concept> inputSet, Set<Concept> goalSet) {
        return inputSet.containsAll(goalSet);
    }

    public void removeFromSet(Set<Concept> srcSet, Concept objToBeRemoved) {
        Iterator<Concept> iter = srcSet.iterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            if (objToBeRemoved.getName().equalsIgnoreCase(c.getName())) {
                iter.remove();
                break;
            }
        }
    }

    private void removeFromSet(Set<Concept> srcSet, Set<Concept> setToBeRemoved) {
        for (Concept objToBeRemoved : setToBeRemoved) {
            Iterator<Concept> iter = srcSet.iterator();
            while (iter.hasNext()) {
                Concept c = iter.next();
                if (objToBeRemoved.getName().equalsIgnoreCase(c.getName())) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    private void addToSet(Set<Concept> tgtSet, Set<Concept> srcSet) {
        Iterator<Concept> iter = srcSet.iterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            boolean found = false;
            for (Concept temp : tgtSet) {
                if (temp.getName().equalsIgnoreCase(c.getName())) {
                    found = true;
                    iter.remove();
                    break;
                }
            }
            if (!found) {
                tgtSet.add(c);
            }
        }
    }

    private boolean checkOtherInputsOfWS(Set<Concept> serviceIpt, Map<String, Service> serviceMap) {
        boolean retVal = compareSets(serviceIpt, goalSet);
        if (!retVal) {
            for (Service service : getPossibleWSToBeConsumed(serviceIpt, serviceMap)) {
                int tempCount = 0;
                Set<Param> paramSet = service.getInputParamSet();
                for (Param param : paramSet) {
                    Concept dummy = conceptMap.get(param.getThing().getType());
                    for (Concept c : serviceIpt) {
                        if (dummy.getName().equalsIgnoreCase(c.getName())) {
                            tempCount++;
                        }
                    }
                }
                if (tempCount == paramSet.size())
                    return true;
            }
            return false;
        } else {
            return true;
        }
    }

    private Set<Service> getPossibleWSToBeConsumed(Set<Concept> InputSet, Map<String, Service> serviceMap) {
        Set<Service> possibleWSs = new HashSet<>();

        for (Entry<String, Service> service : serviceMap.entrySet()) {
            if (InputSet.containsAll(service.getValue().getInputConceptSet())) {
                possibleWSs.add(service.getValue());
            }
        }

        return possibleWSs;
    }

    private Set<Service> getWSThatMatchesAtLeastOneInput(Set<Concept> InputSet, Map<String, Service> serviceMap) {
        Set<Service> possibleWSs = new HashSet<>();

        for (Entry<String, Service> service : serviceMap.entrySet()) {
            for (Concept str : service.getValue().getInputConceptSet()) {
                if (InputSet.contains(str)) {
                    possibleWSs.add(service.getValue());
                    break;
                }
            }
        }

        return possibleWSs;
    }

    private Set<Service> getWSThatMatchesAtLeastOneOutput(Set<Concept> outputSet, Map<String, Service> serviceMap) {
        Set<Service> possibleWSs = new HashSet<>();

        for (Entry<String, Service> service : serviceMap.entrySet()) {
            int count = 0;
            Set<Concept> set = service.getValue().getOutputConceptSet();
            if (set.size() == outputSet.size()) {
                for (Concept c : set) {
                    if (outputSet.contains(c)) {
                        count++;
                    }
                }
                if (count == outputSet.size()) {
                    possibleWSs.add(service.getValue());
                }
            }
        }

        return possibleWSs;
    }

    private boolean expandGraph(Set<Service> servicesToBeTried, Map<String, Service> serviceMap, Set<Concept> inputSet,
                                List<String> servicesConsumed, Set<Service> aggrPrevServices, boolean enableTimer) {
        int count = 0;
        Set<Concept> outputSet = null;
        String serviceNameToBeTried = null;
        boolean inputPresent = false;
        for (Service service : servicesToBeTried) {
            serviceNameToBeTried = service.getName();
            servicesConsumed.add(serviceNameToBeTried);
            outputSet = new HashSet<>(serviceMap.get(serviceNameToBeTried).getOutputConceptSet());
            int prevSize = inputSet.size();
            addToSet(inputSet, outputSet);
            if (compareSets(inputSet, goalSet)) {
                aggrPrevServices.add(service);
                if (!enableTimer) return true;
            }
            if (inputSet.size() == prevSize) {
                count++;
                servicesConsumed.remove(serviceNameToBeTried);
                serviceMap.remove(serviceNameToBeTried);
            } else {
                inputPresent = checkOtherInputsOfWS(inputSet, serviceMap);
                if (!inputPresent) {
                    count++;
                    serviceMap.remove(serviceNameToBeTried);
                    servicesConsumed.remove(serviceNameToBeTried);
                    removeFromSet(inputSet, outputSet);
                } else {
                    Set<Concept> dummySet = new HashSet<>(serviceMap.get(serviceNameToBeTried).getOutputConceptSet());
                    aggrPrevServices.addAll(servicesToBeTried);
                    Set<Service> servicesToBeConsumed = getPossibleWSToBeConsumed(inputSet, serviceMap);
                    Set<Service> probableServices = getWSThatMatchesAtLeastOneInput(dummySet, serviceMap);
                    Set<Service> svcsProducingOutput = getWSThatMatchesAtLeastOneOutput(dummySet, serviceMap);
                    for (Service str : aggrPrevServices) {
                        servicesToBeConsumed.remove(str);
                        probableServices.remove(str);
                        svcsProducingOutput.remove(str);
                    }
                    probableServices.removeAll(servicesToBeConsumed);
                    svcsProducingOutput.removeAll(servicesToBeConsumed);
                    Set<Concept> dummyInputSet = new HashSet<>(inputSet);
                    LinkedList<String> list = new LinkedList<>();
                    long start = System.currentTimeMillis();
                    if (!expandGraph(servicesToBeConsumed, serviceMap, dummyInputSet, list, aggrPrevServices, false)) {
                        if (probableServices.size() != 0 && svcsProducingOutput.size() != 0
                                || probableServices.size() == 0 && svcsProducingOutput.size() == 0
                                || probableServices.size() == 0) {
                            count++;
                            serviceMap.remove(serviceNameToBeTried);
                            servicesConsumed.remove(serviceNameToBeTried);
                            removeFromSet(inputSet, outputSet);
                        }
                    }
                    if (enableTimer) {
                        backTrackingTime += (System.currentTimeMillis() - start);
                    }
                }
            }
        }
        return count != servicesToBeTried.size();
    }
}
