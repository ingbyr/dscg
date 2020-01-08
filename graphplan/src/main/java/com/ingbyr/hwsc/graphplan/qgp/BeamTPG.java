package com.ingbyr.hwsc.graphplan.qgp;

import com.ingbyr.hwsc.common.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class BeamTPG {

    private static final Service dummyService = new Service("I");
    private static final Set<Service> dummyServiceSet = new HashSet<>();

    static {
        dummyServiceSet.add(dummyService);
    }

    @NonNull
    private DataSetReader reader;

    private int level = 0;

    private static final double pathUtilityWeight = 0.5;

    private static final double goalWeight = 1 - pathUtilityWeight;


    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    private static class Label {
        Concept concept;

        Set<Service> services;

        double cost;

        double h;

        public Label(Set<Service> services, double cost, Concept concept) {
            this.services = new HashSet<>(services);
            this.cost = cost;
            this.concept = concept;
        }

        public Label(Label label, Concept concept) {
            this.services = new HashSet<>(label.services);
            this.cost = label.cost;
            this.concept = concept;
        }

        @Override
        public String toString() {
            return "{" + concept + ", " + services + ", " + cost + "}";
        }


        public static Label copyOf(Label label) {
            Label newLabel = new Label();
            newLabel.concept = label.concept;
            newLabel.services = new HashSet<>(label.services);
            newLabel.cost = label.cost;
            newLabel.h = label.h;
            return newLabel;
        }
    }

    @EqualsAndHashCode
    private static class PLevel implements Serializable {

        Set<Label> labels = new HashSet<>();

        @EqualsAndHashCode.Exclude
        Map<Concept, Set<Label>> conceptLabelsMap = new HashMap<>();

        void addLabel(Label label) {
            labels.add(label);

            // Update concept labels map
            if (conceptLabelsMap.containsKey(label.concept)) {
                conceptLabelsMap.get(label.concept).add(label);
            } else {
                Set<Label> labels = new HashSet<>();
                labels.add(label);
                conceptLabelsMap.put(label.concept, labels);
            }
        }

        public boolean containsAll(Set<Concept> inputConceptSet) {
            return conceptLabelsMap.keySet().containsAll(inputConceptSet);
        }

        @Override
        public String toString() {
            return "PLevel{" + labels + "}";
        }

        public static PLevel copyOf(PLevel pLevel) {
            PLevel newPLevel = new PLevel();
            for (Label label : pLevel.labels) {
                newPLevel.labels.add(Label.copyOf(label));
            }
            newPLevel.conceptLabelsMap = new HashMap<>(pLevel.conceptLabelsMap);
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
        Instant startTime = Instant.now();

        TPG gh = buildTPG();
        // displayPALevel(gh);

        CombService combService = resultServicesList(gh);

        Instant endTime = Instant.now();
        log.info("Runtime {}", Duration.between(startTime, endTime).toMillis() / 1000.0);
    }

    private CombService resultServicesList(TPG gh) {
        PLevel lastPLevel = gh.pList.get(gh.level);

        List<List<Label>> goalLabels = new ArrayList<>();
        for (Concept concept : reader.getGoalSet()) {
            List<Label> labels = new ArrayList<>(lastPLevel.conceptLabelsMap.get(concept));
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
            log.debug("Processing level {}", level);
            // Pre p level
            PLevel pLevel = PLevel.copyOf(pList.get(level - 1));

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
                    parentLabels.addAll(pList.get(level - 1).conceptLabelsMap.get(in));
                }
                // System.out.println(service + " parents " + parentLabels);

                for (Concept concept : service.getOutputConceptSet()) {

                    // TODO beam parents
                    parentLabels = beam(parentLabels);
                    for (Label parentLabel : parentLabels) {
                        Label outputLabel = Label.copyOf(parentLabel);
                        outputLabel.concept = concept;
                        outputLabel.services.add(service);
                        outputLabel.cost = outputLabel.cost + service.getCost();
                        pLevel.addLabel(outputLabel);
                    }
                }
            }

            // System.out.println("P level: ");
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

    private Set<Label> beam(Set<Label> parentLabels) {
        // pLevelLabels = pLevelLabels.stream().limit(5).collect(Collectors.toSet());;
        // // TODO Top k selection
        // Map<Concept, Set<Label>> cache = new HashMap<>();
        //
        // for (Label pLevelLabel : pLevelLabels) {
        //     Set<Label> labels = cache.get(pLevelLabel.concept);
        //     if (labels == null) {
        //         labels = new HashSet<>();
        //         labels.add(pLevelLabel);
        //         cache.put(pLevelLabel.concept, labels);
        //     } else {
        //         labels.add(pLevelLabel);
        //     }
        // }
        //
        // // End of top k selection
        // cache.forEach((concept, labels) -> {
        //     log.debug("PLevel {} parents :{}", concept, labels);
        //     double pu = pathUtility(labels) * beamPathUtilityWeight;
        //     double pug = percentageOfUnsatisfiedGoals(labels) * beamGoalDistanceWeight;
        //
        //     log.debug("Path utility {}", pathUtility);
        // });
        // log.debug("");
        return parentLabels.stream().limit(5).collect(Collectors.toSet());
    }

    private double pathUtility(Set<Label> labels) {
        double pathUtility = 0.0;
        for (Label label : labels) {
            pathUtility += label.cost;
        }
        log.debug("Path utility {}", pathUtility);
        return pathUtility;
    }

    private double percentageOfUnsatisfiedGoals(Set<Label> labels) {
        double goalSize = reader.getGoalSet().size();
        Set<Concept> partGoalSet = new HashSet<>();
        for (Label label : labels) {
            // partGoalSet.addAll(Sets.intersection(label.service.getOutputConceptSet(), reader.getGoalSet()));
        }
        log.debug("Part goal size {}", partGoalSet.size());
        return (goalSize - partGoalSet.size()) / goalSize;
    }

    private void displayPLevel(PLevel pLevel, int level) {
        pLevel.conceptLabelsMap.forEach(((concept, labels) -> {
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
            log.info("============== level {}/{} =============", i, gh.level);
            log.info("A level : {}", aList.get(i).size());
            for (Service service : aList.get(i).services) {
                log.debug("{}, in {}, out {}", service, service.getInputConceptSet(), service.getOutputConceptSet());
            }

            PLevel pLevel = pList.get(i);
            log.info("P level: {}", pLevel.labels.size());
            for (Label label : pLevel.labels) {
                log.debug("{}", label);
            }
        }
    }
}

