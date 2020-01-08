package com.ingbyr.hwsc.graphplan.qgp;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class QPG {

    private static final Service dummyService = new Service("I");

    @NonNull
    private DataSetReader reader;

    private Queue<LWGNode> nodeQueue = new LinkedList<>();

    private Set<LWGNode> newPreNodes;

    private int level = 0;

    private LWGNode startNode;

    private LWGNode endNode;

    // private static final double beamPathUtilityWeight = 0.5;
    // private static final double beamGoalDistanceWeight = 1 - beamPathUtilityWeight;


    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    private static class Label {
        Concept concept;

        Service service;

        int level;

        double cost;

        public Label(Service service, int level, double cost, Concept concept) {
            this.service = service;
            this.level = level;
            this.cost = cost;
            this.concept = concept;
        }

        public Label(Label label, Concept concept) {
            this.service = label.service;
            this.level = label.level;
            this.cost = label.cost;
            this.concept = concept;
        }

        @Override
        public String toString() {
            return "{" + concept + ", " + service + ", " + level + ", " + cost + "}";
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

        void addLabels(Set<Label> labels) {
            for (Label label : labels) {
                addLabel(label);
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
            newPLevel.labels = new HashSet<>(pLevel.labels);
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

    @AllArgsConstructor
    private static class PLPG {
        List<PLevel> pList;
        List<ALevel> aList;
        int level;
    }

    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class LWGNode {

        @NonNull
        Set<Concept> concepts;

        @NonNull
        Set<Label> labels;

        int level;

        public static LWGNode copyOf(LWGNode node, int level) {
            return new LWGNode(new HashSet<>(node.concepts), new HashSet<>(node.labels), level);
        }
    }

    public static class LWGEdge extends DefaultWeightedEdge {

        @Getter
        @Setter
        Set<Service> services;

        @Override
        protected LWGNode getSource() {
            return (LWGNode) super.getSource();
        }

        @Override
        protected LWGNode getTarget() {
            return (LWGNode) super.getTarget();
        }

        @Override
        protected double getWeight() {
            return super.getWeight();
        }
    }

    /**
     * Main
     */
    public void qosWSC() {
        Instant startTime = Instant.now();
        PLPG gh = buildPLPG();
        displayPALevel(gh);
        Graph<LWGNode, LWGEdge> lwg = PLPG2PLG(gh);
        findShortestPath(lwg);
        Instant endTime = Instant.now();
        log.info("Runtime {}", Duration.between(startTime, endTime).toMillis() / 1000.0);
    }

    private void findShortestPath(Graph<LWGNode, LWGEdge> lwg) {
        log.info("Finding shortest path...");
        DijkstraShortestPath<LWGNode, LWGEdge> finder = new DijkstraShortestPath<>(lwg);
        GraphPath<LWGNode, LWGEdge> shortestPath = finder.getPath(startNode, endNode);
        List<Service> services = bestServices(shortestPath);
        log.info("Best service chain {}", services);
        log.info("Best service cost {}", calcCost(services));
        log.info("Best service qos {}", QosUtils.mergeQos(services));
    }

    private double calcCost(Collection<Service> services) {
        double cost = 0.0;
        for (Service service : services) {
            cost += service.getCost();
        }
        return cost;
    }

    public static List<Service> bestServices(GraphPath<LWGNode, LWGEdge> path) {
        List<Service> services = new LinkedList<>();
        for (LWGEdge edge : path.getEdgeList()) {
            edge.getServices().forEach(service -> {
                if (!"#".equals(service.getName()))
                    services.add(service);
            });
        }
        return services;
    }

    private Graph<LWGNode, LWGEdge> PLPG2PLG(PLPG plpg) {
        Graph<LWGNode, LWGEdge> lwg = new SimpleDirectedWeightedGraph<>(LWGEdge.class);

        List<List<Label>> goalLabel = new ArrayList<>(reader.getGoalSet().size());

        PLevel pLevelLast = plpg.pList.get(plpg.pList.size() - 1);
        for (Concept goal : reader.getGoalSet()) {
            List<Label> singleGoalLabel = new ArrayList<>();
            for (Label label : pLevelLast.conceptLabelsMap.get(goal)) {
                singleGoalLabel.add(new Label(label, goal));
            }
            goalLabel.add(singleGoalLabel);
        }
        for (List<Label> labels : goalLabel) {
            log.debug("Goal labels: {}", labels);
        }

        Set<Set<Label>> allComb = CombineUtils.combine(goalLabel);
        log.info("Goal nodes size {}", allComb.size());
        for (Set<Label> labels : allComb) {
            log.debug("Goal Comb: {}", labels);
        }

        startNode = new LWGNode(new HashSet<>(), new HashSet<>(), 0);
        endNode = new LWGNode(new HashSet<>(), new HashSet<>(), level + 1);

        lwg.addVertex(endNode);
        for (Set<Label> labels : allComb) {
            LWGNode node = new LWGNode(reader.getGoalSet(), labels, level);
            lwg.addVertex(node);
            nodeQueue.add(node);

            // Link goal node to end node
            LWGEdge emptyEdge = lwg.addEdge(node, endNode);
            emptyEdge.services = new HashSet<>();
            lwg.setEdgeWeight(emptyEdge, 0.0);
        }

        for (; level > 0; level--) {
            int nodeSize = nodeQueue.size();
            newPreNodes = new LinkedHashSet<>();
            int nodeIndex = 0;
            log.info("===== [Level-{}]=====", level);
            while (!nodeQueue.isEmpty()) {
                LWGNode node = nodeQueue.poll();
                log.info("----- [Level-{}] node {}/{}  -----", level, nodeIndex++, nodeSize);
                log.debug("Node {}", node);
                createPreNodesForNode(node, plpg, lwg);
            }
            log.info("===== [Level-{}] Add {} new pre nodes =====", level, newPreNodes.size());
            newPreNodes.forEach(nodeQueue::offer);
        }

        // Link to start node
        for (LWGNode inputNode : newPreNodes) {
            lwg.addVertex(startNode);
            LWGEdge edge = lwg.addEdge(startNode, inputNode);
            edge.services = new HashSet<>();
            lwg.setEdgeWeight(edge, 0.0);
        }
        return lwg;
    }

    private void createPreNodesForNode(LWGNode node, PLPG plpg, Graph<LWGNode, LWGEdge> plg) {

        Set<Label> afterRemovedLabels = new HashSet<>(node.labels);
        Iterator<Label> labelItr = afterRemovedLabels.iterator();
        Set<Label> edgeLabels = new HashSet<>();
        while (labelItr.hasNext()) {
            Label label = labelItr.next();
            if (label.level == level) {
                edgeLabels.add(label);
                labelItr.remove();
            }
        }

        Set<Concept> requiredConcepts = new HashSet<>();
        Set<Concept> removedConcepts = new HashSet<>();
        for (Label label : edgeLabels) {
            requiredConcepts.addAll(label.service.getInputConceptSet());

            if (removedConcepts.add(label.concept)) {
                log.debug("Remove {} from node and add to edge", label);
            }
        }

        Set<Concept> preNodeConcepts = new HashSet<>(node.concepts);
        preNodeConcepts.removeAll(removedConcepts);
        preNodeConcepts.addAll(requiredConcepts);
        log.debug("new required concept {}", requiredConcepts);

        if (requiredConcepts.isEmpty()) {
            log.debug("copy node as pre node");
            LWGNode preNode = LWGNode.copyOf(node, level - 1);
            plg.addVertex(preNode);
            LWGEdge edge = plg.addEdge(preNode, node);
            edge.services = new HashSet<>();
            plg.setEdgeWeight(edge, 0.0);

            newPreNodes.add(preNode);
        } else {
            List<List<Label>> newConceptLabels = new ArrayList<>(requiredConcepts.size());
            for (Concept requiredConcept : requiredConcepts) {
                newConceptLabels.add(
                        new ArrayList<>(plpg.pList.get(level - 1).conceptLabelsMap.get(requiredConcept))
                );
            }

            if (!afterRemovedLabels.isEmpty()) {
                newConceptLabels.add(new ArrayList<>(afterRemovedLabels));
            }


            Set<Set<Label>> newNodeLabelSet = CombineUtils.combine(newConceptLabels);
            for (Set<Label> nodeLabels : newNodeLabelSet) {
                LWGNode preNode = new LWGNode(preNodeConcepts, nodeLabels, level - 1);
                boolean newNode = plg.addVertex(preNode);

                if (newNode)
                    log.debug("add pre node {}", preNode);
                else
                    log.debug("exists node {}", preNode);

                LWGEdge edge = plg.addEdge(preNode, node);

                double cost = addServices2Edge(edgeLabels, edge);
                plg.setEdgeWeight(edge, cost);

                newPreNodes.add(preNode);
            }
        }
    }

    private double addServices2Edge(Set<Label> labels, LWGEdge edge) {
        Set<Service> serviceSet = new HashSet<>();
        double cost = 0.0;
        for (Label label : labels) {
            serviceSet.add(label.service);
            cost += label.cost;
        }
        edge.services = serviceSet;
        return cost;
    }

    private PLPG buildPLPG() {
        Set<Service> serviceSet = new HashSet<>(reader.getServiceMap().values());

        List<PLevel> pList = new ArrayList<>();
        List<ALevel> aList = new ArrayList<>();

        PLevel p0 = new PLevel();

        for (Concept inputConcept : reader.getInputSet()) {
            Label dummyLabel = new Label(dummyService, 0, 0, inputConcept);
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
            // Pre p level
            PLevel pPreLevel = PLevel.copyOf(pList.get(level - 1));

            // A_i
            Set<Service> aLevelServices = serviceSet.stream()
                    .filter(service -> pPreLevel.containsAll(service.getInputConceptSet()))
                    .collect(Collectors.toSet());

            ALevel aLevel = new ALevel();
            aLevel.setServices(aLevelServices);
            aList.add(aLevel);


            // SA
            Set<Service> sa = new HashSet<>();
            for (Service service : aList.get(level).services) {
                if (!Collections.disjoint(service.getInputConceptSet(), sp)) {
                    sa.add(service);
                }
            }
            aLevel.setServices(sa); // TODO FIXME


            // SP
            sp = new HashSet<>();
            for (Service service : sa) {
                sp.addAll(service.getOutputConceptSet());
            }

            // P'
            Set<Label> ppLabels = new HashSet<>();
            for (Concept p : sp) {
                for (Service service : sa) {
                    if (service.getOutputConceptSet().contains(p)) {
                        ppLabels.add(new Label(service, level, service.getCost(), p));
                    }
                }
            }

            // P''
            Set<Label> pppLabels = new HashSet<>();
            for (Label pPreLabel : pPreLevel.labels) {
                for (Label ppLabel : ppLabels) {
                    if (pPreLabel.concept.equals(ppLabel.concept)) {
                        pppLabels.add(pPreLabel);
                        pppLabels.add(ppLabel);
                    }
                }
            }


            // System.out.println("P_i-1: " + pLevelPre.labels);
            // System.out.println("P': " + ppLabels);
            // System.out.println("P'': " + pppLabels);

            Set<Label> pLevelPreCopy = new HashSet<>(pPreLevel.labels);
            pLevelPreCopy.removeAll(ppLabels);
            // System.out.println("P_i-1 - P': " + pLevelPreCopy);

            Set<Label> ppLevelCopy = new HashSet<>(ppLabels);
            ppLevelCopy.removeAll(pPreLevel.labels);
            // System.out.println("P'- P_i-1: " + ppLevelCopy);

            pLevelPreCopy.addAll(ppLevelCopy);
            pLevelPreCopy.addAll(pppLabels);
            Set<Label> pLevelLabels = new HashSet<>(pLevelPreCopy);
            // System.out.println("P: " + pLevelLC);

            // P_i
            PLevel pLevel = new PLevel();
            pLevel.addLabels(pLevelLabels);
            pList.add(pLevel);

            // displayPLevel(pLevel, level);
            // TODO fixed point
            if (pLevel.containsAll(reader.getGoalSet())) {
                reachable++;
            }
            if (aList.get(level).equals(aList.get(level - 1)) || reachable >= 2)
                break;
            else
                level++;
        }

        return new PLPG(pList, aList, level);
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

    private void displayPALevel(PLPG gh) {
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

