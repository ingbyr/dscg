package com.ingbyr.hwsc.graphplan.qgp;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class QPG {

    private static final Service dummyService = new Service("I");

    private DataSetReader reader;

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Label {
        Service a;
        int l;
        double c;

        static Set<Label> union(Set<Label> label1, Set<Label> label2) {
            Set<Label> labels = new HashSet<>(label1.size() + label2.size());
            labels.addAll(label1);
            labels.addAll(label2);
            return labels;
        }

        @Override
        public String toString() {
            return "{" + a + ", " + l + ", " + c + "}";
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class LabeledConcept {

        @Getter
        Concept concept;

        @Getter
        @EqualsAndHashCode.Exclude
        Set<Label> labels;

        @Override
        public String toString() {
            return "Concept: " + concept + ", Labels: " + labels;
        }
    }

    @EqualsAndHashCode
    private static class PLevel {

        Set<LabeledConcept> labeledConcepts = new HashSet<>();

        Map<Concept, LabeledConcept> cache = new HashMap<>();

        void addConcept(LabeledConcept labeledConcept) {
            labeledConcepts.add(labeledConcept);
            cache.put(labeledConcept.concept, labeledConcept);
        }

        void setLabeledConcepts(Set<LabeledConcept> labeledConcepts) {
            this.labeledConcepts = labeledConcepts;
            for (LabeledConcept labeledConcept : labeledConcepts) {
                cache.put(labeledConcept.concept, labeledConcept);
            }
        }

        public boolean containsAll(Set<Concept> inputConceptSet) {
            return cache.keySet().containsAll(inputConceptSet);
        }

        @Override
        public String toString() {
            return "PLevel{" + labeledConcepts + '}';
        }
    }

    @AllArgsConstructor
    private static class PLPG {
        List<PLevel> pList;
        List<Set<Service>> aList;
        int level;
    }

    /**
     * Main
     */
    public void qosWSC() {
        PLPG gh = qosGraphPlan();
        displayPALevel(gh);
        graphConversion(gh);
    }

    private void graphConversion(PLPG gh) {
        int n = gh.pList.size() - 1;

        List<List<Label>> goalLabel = new ArrayList<>(reader.getGoalSet().size());

        PLevel pLevelLast = gh.pList.get(gh.pList.size() - 1);
        for (Concept goal : reader.getGoalSet()) {
            goalLabel.add(new ArrayList<>(pLevelLast.cache.get(goal).labels));
        }
        for (List<Label> labels : goalLabel) {
            log.debug("Goal labels: {}", labels);
        }

        Set<Set<Label>> allComb = combineLabel(goalLabel);
        for (Set<Label> labels : allComb) {
            log.debug("Goal Comb: {}", labels);
        }

        int i = n;
        while (i == 1) {
            i--;
        }
    }


    private Set<Set<Label>> combineLabel(List<List<Label>> labels) {
        Set<Set<Label>> labelCombinationResult = new LinkedHashSet<>();
        List<Label> labelCombinationStepResult = new LinkedList<>();
        combineLabelHelper(labels, 0, labelCombinationResult, labelCombinationStepResult);
        return labelCombinationResult;
    }

    private void combineLabelHelper(List<List<Label>> labels,
                                    int depth,
                                    Set<Set<Label>> labelCombinationResult,
                                    List<Label> labelCombinationStepResult) {

        // // FIXME auto stop combine service when size too big
        // if (services.size() == 0 || serviceCombinationResult.size() > maxPreNodeSize)
        //     return;

        for (int i = 0; i < labels.get(depth).size(); i++) {
            Label label = labels.get(depth).get(i);
            try {
                labelCombinationStepResult.set(depth, label);
            } catch (IndexOutOfBoundsException e) {
                labelCombinationStepResult.add(label);
            }

            if (depth == labels.size() - 1) {
                // create new one because that data will be reset in next search
                labelCombinationResult.add(Sets.newLinkedHashSet(labelCombinationStepResult));
            } else {
                combineLabelHelper(labels, depth + 1, labelCombinationResult, labelCombinationStepResult);
            }
        }
    }


    /**
     * Build PLPG layer by layer
     *
     * @return
     */
    private PLPG qosGraphPlan() {
        Set<Service> serviceSet = new HashSet<>(reader.getServiceMap().values());

        List<PLevel> pList = new ArrayList<>();
        List<Set<Service>> aList = new ArrayList<>();

        PLevel p0 = new PLevel();
        Set<Label> dummyLabel = new HashSet<>();
        dummyLabel.add(new Label(dummyService, 0, 0));
        for (Concept concept : reader.getInputSet()) {
            p0.addConcept(new LabeledConcept(concept, dummyLabel));
        }

        pList.add(p0);
        aList.add(new HashSet<>());

        Set<Concept> sp = new HashSet<>(reader.getInputSet());
        int level = 1;
        int reachable = 0;

        while (true) {
            // Pre p level
            PLevel pLevelPre = pList.get(level - 1);

            // A_i
            Set<Service> aLevel = serviceSet.stream().filter(
                    service -> pLevelPre.containsAll(service.getInputConceptSet())
            ).collect(Collectors.toSet());
            aList.add(aLevel);

            // SA
            Set<Service> sa = new HashSet<>();
            for (Service service : aLevel) {
                if (!Collections.disjoint(service.getInputConceptSet(), sp)) {
                    sa.add(service);
                }
            }

            // SP
            sp = new HashSet<>();
            for (Service service : sa) {
                sp.addAll(service.getOutputConceptSet());
            }

            // P'
            PLevel ppLevel = new PLevel();
            for (Concept p : sp) {
                Set<Label> ppLabel = new HashSet<>();
                for (Service a : aLevel) {
                    if (a.getOutputConceptSet().contains(p)) {
                        ppLabel.add(new Label(a, level, a.getCost()));
                    }
                }
                ppLevel.addConcept(new LabeledConcept(p, ppLabel));
            }
            // displayPLevel(ppLevel, -1);

            // P''
            PLevel pppLevel = new PLevel();
            for (LabeledConcept pPre : pLevelPre.labeledConcepts) {
                for (LabeledConcept pp : ppLevel.labeledConcepts) {
                    if (pPre.concept.equals(pp.concept)) {
                        pppLevel.addConcept(new LabeledConcept(pPre.concept, Label.union(pPre.labels, pp.labels)));
                    }
                }
            }

            // displayPLevel(ppLevel, -2);

            // P_i
            PLevel pLevel = new PLevel();
            Set<LabeledConcept> lc = Sets.union(
                    Sets.union(
                            Sets.difference(pLevelPre.labeledConcepts, ppLevel.labeledConcepts),
                            Sets.difference(ppLevel.labeledConcepts, pLevelPre.labeledConcepts)),
                    pppLevel.labeledConcepts);

            pLevel.setLabeledConcepts(lc);
            pList.add(pLevel);

            // displayPLevel(pLevelPre, i - 1);
            // displayALevel(aLevel, i);
            // displayPLevel(pLevel, i);

            // TODO fixed point
            if (pLevel.containsAll(reader.getGoalSet())) {
                reachable++;
            }
            if (aLevel.equals(aList.get(level - 1)) || reachable >= 2)
                break;
            else
                level++;
        }

        return new PLPG(pList, aList, level);
    }

    private void displayPLevel(PLevel pLevel, int level) {
        for (LabeledConcept labeledConcept : pLevel.labeledConcepts) {
            System.out.println("[" + level + "] " + labeledConcept);
        }
        System.out.println();
    }

    private void displayALevel(Set<Service> aLevel, int level) {
        for (Service service : aLevel) {
            System.out.println("[" + level + "] " + service);
        }
        System.out.println();
    }

    private void displayPALevel(PLPG gh) {
        List<PLevel> pList = gh.pList;
        List<Set<Service>> aList = gh.aList;
        for (int i = 0; i < pList.size(); i++) {
            log.debug("");
            log.debug("============== level {}/{} =============", i, gh.level);
            log.debug("Services {}: {}", aList.get(i).size(), aList.get(i));
            log.debug("Labeled concepts: {}", pList.get(i).labeledConcepts.size());
            for (LabeledConcept p : pList.get(i).labeledConcepts) {
                log.debug("{}", p);
            }
        }
        log.debug("");
    }

    public static void main(String[] args) {
        DataSetReader reader = new XmlDatasetReader();
        reader.setDataset(Dataset.wsc2020_01);
        // reader.setDataset(Dataset.wsc2009_01);
        reader.getServiceMap().forEach((s, ser) -> {
            log.debug("service {}, in {}, out {}", s, ser.getInputConceptSet(), ser.getOutputConceptSet());
        });
        log.debug("problem in {}, out {}", reader.getInputSet(), reader.getGoalSet());

        QPG qpg = new QPG(reader);
        qpg.qosWSC();
    }
}

