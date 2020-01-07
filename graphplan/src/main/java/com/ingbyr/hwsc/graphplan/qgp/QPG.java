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
            return "Label{" +
                    "a=" + a +
                    ", l=" + l +
                    ", c=" + c +
                    '}';
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
            return "LabeledConcept{" +
                    "concept=" + concept +
                    ", labels=" + labels +
                    '}';
        }
    }

    @EqualsAndHashCode
    private static class PLevel {

        Set<LabeledConcept> labeledConcepts = new HashSet<>();

        Set<Concept> concepts = new HashSet<>();

        void addConcept(LabeledConcept labeledConcept) {
            labeledConcepts.add(labeledConcept);
            concepts.add(labeledConcept.concept);
        }

        void setLabeledConcepts(Set<LabeledConcept> labeledConcepts) {
            this.labeledConcepts = labeledConcepts;
            this.concepts = labeledConcepts.stream().map(LabeledConcept::getConcept).collect(Collectors.toSet());
        }

        @Override
        public String toString() {
            return "PLevel{" +
                    "labeledConcepts=" + labeledConcepts +
                    ", concepts=" + concepts +
                    '}';
        }
    }

    /**
     * Main
     */
    public void qosWSC() {
        qosGraphPlan();
    }

    /**
     * Build PLPG layer by layer
     *
     * @return
     */
    private void qosGraphPlan() {
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
        int i = 1;

        while (true) {
            // Pre p level
            PLevel pLevelPre = pList.get(i - 1);

            // A_i
            Set<Service> aLevel = serviceSet.stream().filter(
                    service -> pLevelPre.concepts.containsAll(service.getInputConceptSet())
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
                        ppLabel.add(new Label(a, i, a.getCost()));
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

            displayPLevel(pLevelPre, i - 1);
            displayALevel(aLevel, i);
            displayPLevel(pLevel, i);

            if (aLevel.equals(aList.get(i - 1))) {
                break;
            } else {
                i++;
            }
        }
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

    public static void main(String[] args) {
        DataSetReader reader = new XmlDatasetReader();
        reader.setDataset(Dataset.wsc2020_01);
        reader.getServiceMap().forEach((s, ser) -> {
            log.debug("service {}, in {}, out {}", s, ser.getInputConceptSet(), ser.getOutputConceptSet());
        });
        log.debug("problem in {}, out {}", reader.getInputSet(), reader.getGoalSet());

        QPG qpg = new QPG(reader);
        qpg.qosWSC();
    }
}

