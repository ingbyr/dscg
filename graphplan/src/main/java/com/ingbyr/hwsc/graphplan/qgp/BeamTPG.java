package com.ingbyr.hwsc.graphplan.qgp;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class BeamTPG {

    private static final Service dummyService = new Service("I");

    private static final Set<Service> dummyServiceSet = new HashSet<>();

    private static final double pathUtilityWeight = 0.5;

    private static final double goalWeight = 1 - pathUtilityWeight;

    public static int BEAM_WIDTH = 5;

    static {
        dummyServiceSet.add(dummyService);
    }

    @NonNull
    private DataSetReader reader;

    private int level = 0;

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
                if (labelSet.size() > BeamTPG.BEAM_WIDTH) {
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

    @ToString
    private static class CombService implements Comparable<CombService> {

        Set<Service> services = new HashSet<>();

        double cost = -1;

        @Override
        public int compareTo(CombService o) {
            return Double.compare(this.getCost(), o.getCost());
        }

        public void addServices(Set<Service> services) {
            for (Service service : services) {
                if (service != dummyService)
                    this.services.add(service);
            }
        }

        public double getCost() {
            if (cost < 0)
                cost = Service.calcCost(services);
            return cost;
        }
    }

    @AllArgsConstructor
    private static class TPG {
        List<PLevel> pList;
        List<ALevel> aList;
        int level;
    }

    /**
     * Main
     */
    public void beamSearch() {
        log.info("Max beam width {}", BEAM_WIDTH);
        Instant startTime = Instant.now();

        TPG gh = buildTPG();
        displayPALevel(gh);
        CombService combService = resultServicesList(gh);

        Instant endTime = Instant.now();
        log.info("Runtime {}", Duration.between(startTime, endTime).toMillis() / 1000.0);
        log.info("Best comb-service {}", combService);
        log.info("Best comb-service qos {}", QosUtils.mergeQos(combService.services));
    }

    private CombService resultServicesList(TPG gh) {
        PLevel lastPLevel = gh.pList.get(gh.level);

        List<List<Label>> goalLabels = new ArrayList<>();
        for (Concept concept : reader.getGoalSet()) {
            List<Label> labels = new ArrayList<>(lastPLevel.map.get(concept));
            goalLabels.add(labels);
        }

        log.debug("Combine result: ");

        Set<Set<Label>> resultLabels = CombineUtils.combine(goalLabels);
        Queue<CombService> combServiceList = new PriorityQueue<>();
        for (Set<Label> resultLabel : resultLabels) {
            CombService combService = new CombService();
            for (Label label : resultLabel) {
                combService.addServices(label.services);
            }
            combServiceList.add(combService);
        }

        for (CombService combService : combServiceList) {
            log.debug("Combination service: {}", combService);
            log.debug("Qos {}", QosUtils.mergeQos(combService.services));
        }

        return combServiceList.peek();
    }

    private TPG buildTPG() {

        Set<Service> serviceSet = new HashSet<>(reader.getServiceMap().values());

        List<PLevel> pList = new ArrayList<>();
        List<ALevel> aList = new ArrayList<>();

        PLevel p0 = new PLevel();
        for (Concept inputConcept : reader.getInputSet()) {
            Label dummyLabel = new Label(dummyServiceSet, 0.0, inputConcept);
            dummyLabel.h = 0;
            p0.addLabel(dummyLabel);
        }
        pList.add(p0);

        ALevel a0 = new ALevel();
        a0.setServices(new HashSet<>());
        aList.add(a0);

        Set<Concept> sp = new HashSet<>(reader.getInputSet());
        level = 1;
        int reachable = 0;


        while (true) {
            log.info("Processing level {}", level);
            // Pre p level
            PLevel pLevel = PLevel.of(pList.get(level - 1));

            // A_i
            Set<Service> aLevelServices = serviceSet.stream()
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

            // Set<Concept> updateConcepts = new HashSet<>();
            // for (Service service : sa) {
            //     updateConcepts.addAll(service.getOutputConceptSet());
            // }
            // System.out.println("Service " + sa);
            // System.out.println("Update concept" + updateConcepts);
            //
            // System.out.println("Before update:" + pLevel);
            // for (Concept updateConcept : updateConcepts) {
            //     for (Service service : sa) {
            //         if (service.getOutputConceptSet().contains(updateConcept)) {
            //             for (Concept serviceIn : service.getInputConceptSet()) {
            //                 Set<Label> labels = pList.get(level - 1).conceptLabelsMap.get(serviceIn);
            //                 log.debug("Labels {}", labels);
            //                 for (Label label : labels) {
            //                     if (pList.get(0).labels.contains(label) && level != 1)
            //                         continue;
            //                     Label newLabel = Label.copyOf(label);
            //                     newLabel.concept = updateConcept;
            //                     newLabel.services.add(service);
            //                     newLabel.cost += service.getCost();
            //                     log.debug("{}, {} add label {}", service, updateConcept, newLabel);
            //                     pLevel.addLabel(newLabel);
            //                 }
            //             }
            //         }
            //     }
            //
            //     log.debug("Now concept {} label size {}", updateConcept, pLevel.conceptLabelsMap.get(updateConcept).size());
            // }
            // System.out.println("After update:" + pLevel);


            // TODO Beam
            // beamPLevel(pLevel);
            pList.add(pLevel);

            // Fixed point
            if (pLevel.containsAll(reader.getGoalSet())) {
                reachable++;
            }
            if (aList.get(level).equals(aList.get(level - 1)) || reachable >= 2)
                break;
            else
                level++;
        }

        return new TPG(pList, aList, level);
    }

    // private void beamPLevel(PLevel pLevel) {
    //     log.info("Before beam label size {}", pLevel.labelSize);
    //     Set<Label> pLevelLabels = new HashSet<>();
    //
    //     for (Map.Entry<Concept, Set<Label>> entry : pLevel.map.entrySet()) {
    //         Concept concept = entry.getKey();
    //         Set<Label> labels = entry.getValue();
    //         System.out.println(concept + ": " + labels);
    //
    //         // Reduce label set size to width
    //         if (labels.size() > BEAM_WIDTH) {
    //             for (Label label : labels) {
    //                 setH(label);
    //             }
    //             System.out.println("Beam " + concept + ": " + labels);
    //             Queue<Label> labelQueue = new PriorityQueue<>(labels);
    //             Set<Label> beamLabels = new HashSet<>();
    //             for (int i = 0; i < BEAM_WIDTH; i++) {
    //                 beamLabels.add(labelQueue.poll());
    //             }
    //             entry.setValue(beamLabels);
    //             pLevelLabels.addAll(beamLabels);
    //             System.out.println("After beam " + concept + beamLabels);
    //         } else {
    //             pLevelLabels.addAll(labels);
    //         }
    //     }
    //     pLevel.setLabels(pLevelLabels);
    //     log.info("After beam label size {}", pLevel.labelSize);
    // }

    private void setLabelH(Label label) {
        double h = pathUtility(label);
        h += percentageOfUnsatisfiedGoals(label);
        label.h = h;
    }

    private double pathUtility(Label labels) {
        return labels.cost;
    }

    private double percentageOfUnsatisfiedGoals(Label label) {
        double goalSize = reader.getGoalSet().size();
        Set<Concept> partGoalSet = new HashSet<>(Sets.intersection(label.getOutputConceptSet(), reader.getGoalSet()));
        return goalWeight * (goalSize - partGoalSet.size()) / goalSize;
    }

    private void displayPLevel(PLevel pLevel, int level) {
        pLevel.map.forEach(((concept, labels) -> {
            log.debug("[P{}] {}:{}", level, concept, labels);
        }));
        log.debug("");
    }

    private void displayALevel(ALevel aLevel, int level) {
        for (Service service : aLevel.services) {
            System.out.println("[A" + level + "] " + service);
        }
        System.out.println();
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

