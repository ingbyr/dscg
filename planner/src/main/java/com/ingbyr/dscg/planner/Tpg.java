package com.ingbyr.dscg.planner;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.CombineUtils;
import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Service;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Tpg extends AbstractPlanner {

    private static final Service dummyService = new Service("I");

    private static final Set<Service> dummyServiceSet = new HashSet<>();

    static {
        dummyServiceSet.add(dummyService);
    }

    private static final double pathUtilityWeight = 0.5;

    private static final double goalWeight = 1 - pathUtilityWeight;

    public static int BEAM_WIDTH = 1;

    private int level = 0;

    // private Set<Service> serviceSet;

    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    private static class Label implements Comparable<Label> {
        @Setter
        Concept concept;

        Set<Service> services = new HashSet<>();

        double cost;

        double h;

        public Label(Set<Service> services, double cost, Concept concept) {
            this.services = new HashSet<>(services);
            this.cost = cost;
            this.concept = concept;
        }

        Set<Concept> getOutputConceptSet() {
            Set<Concept> out = new HashSet<>();
            for (Service service : services) {
                out.addAll(service.getOutputConceptSet());
            }
            return out;
        }

        void addService(Service service) {
            boolean newService = this.services.add(service);
            if (newService) {
                this.cost += service.getCost();
            }
        }

        @Override
        public String toString() {
            return "{" + concept + ", " + services + ", " + cost + ", " + h + "}";
        }

        @Override
        public int compareTo(Label o) {
            return Double.compare(this.h, o.h);
        }

        public static Label of(Label label) {
            Label newLabel = new Label();
            newLabel.concept = label.concept;
            newLabel.services.addAll(label.services);
            newLabel.cost = label.cost;
            newLabel.h = label.h;
            return newLabel;
        }
    }

    @EqualsAndHashCode
    private static class PLevel {

        int labelSize = 0;

        @EqualsAndHashCode.Exclude
        Map<Concept, Set<Label>> map = new HashMap<>();

        void addLabel(Label label) {
            Set<Label> labelSet = map.get(label.concept);
            if (labelSet == null) {
                labelSet = new HashSet<>();
                labelSet.add(label);
                map.put(label.concept, labelSet);
                labelSize++;
            } else if (labelSet.add(label)) {
                labelSize++;

                // Beam top k
                if (labelSet.size() > Tpg.BEAM_WIDTH) {
                    Label rmLabel = label;
                    for (Label labelInSet : labelSet) {
                        if (labelInSet.h > rmLabel.h) {
                            rmLabel = labelInSet;
                        }
                    }
                    labelSet.remove(rmLabel);
                    log.trace("[Beam] Remove {}", rmLabel);
                }
            }
        }

        void setLabels(Set<Label> labels) {
            labelSize = 0;
            map = new HashMap<>();
            for (Label label : labels) {
                addLabel(label);
            }
        }

        public boolean contains(Label parentLabel) {
            for (Map.Entry<Concept, Set<Label>> entry : map.entrySet()) {
                if (entry.getValue().contains(parentLabel))
                    return true;
            }
            return false;
        }

        public boolean containsAll(Set<Concept> inputConceptSet) {
            for (Concept inputConcept : inputConceptSet) {
                if (!map.containsKey(inputConcept))
                    return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "PLevel{" + map + "}";
        }

        public static PLevel of(PLevel pLevel) {
            PLevel newPLevel = new PLevel();
            for (Map.Entry<Concept, Set<Label>> entry : pLevel.map.entrySet()) {
                for (Label label : entry.getValue()) {
                    newPLevel.addLabel(Label.of(label));
                }
            }
            return newPLevel;
        }
    }

    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static class ALevel {

        Set<Service> services;

        void setServices(Set<Service> services) {
            this.services = new HashSet<>(services);
        }

        public Object size() {
            return services.size();
        }
    }

    @AllArgsConstructor
    private static class TPG {
        List<PLevel> pList;
        List<ALevel> aList;
        int level;
    }

    @Override
    public Solution solve(Set<Concept> inputSet, Set<Concept> goalSet, int boundary) {
        this.inputSet = inputSet;
        this.goalSet = goalSet;
        TPG gh = buildTPG(boundary);
        // displayPALevel(gh);
        return findSolution(gh);
    }

    @Override
    public Planner copy() {
        Planner tpg = new Tpg();
        tpg.setServiceMap(this.serviceMap);
        tpg.setConceptMap(this.conceptMap);
        return tpg;
    }

    private Solution findSolution(TPG gh) {

        if (gh == null) {
            return null;
        }

        PLevel lastPLevel = gh.pList.get(gh.level);

        List<List<Label>> goalLabels = new ArrayList<>();
        for (Concept concept : goalSet) {
            List<Label> labels = new ArrayList<>(lastPLevel.map.get(concept));
            goalLabels.add(labels);
        }

        log.debug("Combine result: ");

        Set<Set<Label>> resultLabels = CombineUtils.combine(goalLabels);
        Queue<Solution> solutionPriorityQueue = new PriorityQueue<>();
        for (Set<Label> resultLabel : resultLabels) {
            Set<Service> services = new HashSet<>();
            for (Label label : resultLabel) {
                services.addAll(label.services);
            }
            services.remove(dummyService);
            Solution combService = new Solution(new ArrayList<>(services), Service.calcCost(services));
            solutionPriorityQueue.add(combService);
        }

        // for (Solution combService : solutionPriorityQueue) {
        //     log.info("Combination service: {}", combService);
        //     log.info("Qos {}", QosUtils.mergeQos(combService.services));
        // }

        return solutionPriorityQueue.peek();
    }

    private TPG buildTPG(int maxLevel) {

        List<PLevel> pList = new ArrayList<>();
        List<ALevel> aList = new ArrayList<>();

        PLevel p0 = new PLevel();
        for (Concept inputConcept : inputSet) {
            Label dummyLabel = new Label(dummyServiceSet, 0.0, inputConcept);
            dummyLabel.h = 0;
            p0.addLabel(dummyLabel);
        }
        pList.add(p0);

        ALevel a0 = new ALevel();
        a0.setServices(new HashSet<>());
        aList.add(a0);

        Set<Concept> sp = new HashSet<>(inputSet);
        level = 1;
        int reachable = 0;


        while (true) {
            log.debug("Processing level {}", level);
            // Pre p level
            PLevel pLevel = PLevel.of(pList.get(level - 1));

            // A_i
            Set<Service> aLevelServices = serviceMap.values().stream()
                    .filter(service -> pList.get(level - 1).containsAll(service.getInputConceptSet()))
                    .collect(Collectors.toSet());

            // SA
            Set<Service> sa = new HashSet<>();
            for (Service service : aLevelServices) {
                if (!Collections.disjoint(service.getInputConceptSet(), sp)) {
                    sa.add(service);
                }
            }
            ALevel aLevel = new ALevel();
            aLevel.setServices(sa);
            aList.add(aLevel);

            // SP
            sp = new HashSet<>();
            for (Service service : sa) {
                sp.addAll(service.getOutputConceptSet());
            }

            // System.out.println("P_i-1 level: " + pList.get(level - 1));
            // System.out.println("A level: " + aLevel);

            for (Service service : aLevel.services) {
                // Parent labels
                Set<Label> parentLabels = new HashSet<>();
                for (Concept in : service.getInputConceptSet()) {
                    parentLabels.addAll(pList.get(level - 1).map.get(in));
                }
                // System.out.println(service + " labels: " + parentLabels);

                for (Concept concept : service.getOutputConceptSet()) {
                    for (Label parentLabel : parentLabels) {
                        // System.out.println(parentLabel);
                        if (p0.contains(parentLabel) && level != 1)
                            continue;

                        Label outputLabel = Label.of(parentLabel);
                        outputLabel.setConcept(concept);
                        outputLabel.addService(service);
                        setLabelH(outputLabel);
                        pLevel.addLabel(outputLabel);
                    }
                }
            }

            pList.add(pLevel);

            // Fixed point
            if (pLevel.containsAll(goalSet)) {
                if (reachable >= 2) {
                    break;
                }
                else {
                    reachable++;
                    level++;
                }
            } else if (aList.get(level).equals(aList.get(level - 1))) {
                return null;
            } else {
                level++;
            }

            if (level > maxLevel) {
                log.info("Over max level {} return null", maxLevel);
                return null;
            }
        }

        return new TPG(pList, aList, level);
    }

    private void setLabelH(Label label) {
        double h = pathUtility(label);
        h += percentageOfUnsatisfiedGoals(label);
        label.h = h;
    }

    private double pathUtility(Label labels) {
        return labels.cost;
    }

    private double percentageOfUnsatisfiedGoals(Label label) {
        double goalSize = goalSet.size();
        Set<Concept> partGoalSet = new HashSet<>(Sets.intersection(label.getOutputConceptSet(), goalSet));
        return goalWeight * (goalSize - partGoalSet.size()) / goalSize;
    }

    private void displayPALevel(TPG gh) {
        List<PLevel> pList = gh.pList;
        List<ALevel> aList = gh.aList;
        for (int i = 0; i < pList.size(); i++) {
            log.debug("============== level {}/{} =============", i, gh.level);
            log.debug("A level : {}", aList.get(i).size());
            for (Service service : aList.get(i).services) {
                log.debug("{}, in {}, out {}", service, service.getInputConceptSet(), service.getOutputConceptSet());
            }

            PLevel pLevel = pList.get(i);
            log.debug("P level: {}", pLevel.labelSize);
            for (Map.Entry<Concept, Set<Label>> entry : pLevel.map.entrySet()) {
                log.debug("{}: {}", entry.getKey(), entry.getValue());
            }
        }
    }
}
