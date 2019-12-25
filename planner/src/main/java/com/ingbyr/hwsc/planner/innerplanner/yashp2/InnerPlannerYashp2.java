package com.ingbyr.hwsc.planner.innerplanner.yashp2;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.NamedObject;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.planner.innerplanner.AbstractInnerPlanner;
import com.ingbyr.hwsc.planner.innerplanner.InnerPlanner;
import com.ingbyr.hwsc.planner.Solution;
import com.ingbyr.hwsc.planner.State;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * @author ingbyr
 */

@Slf4j
public class InnerPlannerYashp2 extends AbstractInnerPlanner implements InnerPlanner {

    private PriorityQueue<YNode> open = new PriorityQueue<>();

    private List<YNode> closed = Lists.newLinkedList();

    private int w;

    private int step;

    private int bMax;

    public InnerPlannerYashp2(Map<String, Service> serviceMap, Map<String, Concept> conceptMap, int w) {
        super(serviceMap, conceptMap);
        this.w = w;
        this.step = 0;
        this.bMax = Integer.MAX_VALUE;
    }

    @Builder
    @EqualsAndHashCode
    public static class YNode implements Comparable<YNode> {

        State state;

        YNode parent;

        List<Service> steps;

        int length;

        int heuristic;

        List<Service> applicable;

        @Override
        public String toString() {
            return "N(" + this.hashCode() + "){" +
                    "state=" + state +
                    ", parent=" + (parent == null ? null : parent.hashCode()) +
                    ", steps=" + steps +
                    ", length=" + length +
                    ", heuristic=" + heuristic +
                    ", applicable=" + applicable +
                    '}';
        }

        @Override
        public int compareTo(YNode node) {
            return Integer.compare(this.heuristic, node.heuristic);
        }
    }

    @Override
    public Solution solve(Set<Concept> inputSet, Set<Concept> goalSet, int boundary) {
        // Reset plan state
        this.inputSet = inputSet;
        this.goalSet = goalSet;
        log.debug("Input set {}", inputSet);
        log.debug("Goal set {}", goalSet);

        if (inputSet.containsAll(goalSet)) {
            log.debug("Input set contains all goal set");
            return new Solution(Lists.newArrayList(), 0);
        }

        open = new PriorityQueue<>();
        closed = Lists.newLinkedList();
        step = 0;
        bMax = boundary;

        // Start plan
        List<Service> plan = planSearch();
        return new Solution(plan, step);
    }

    @Override
    public InnerPlanner copy() {
        return new InnerPlannerYashp2(serviceMap, conceptMap, w);
    }

    /**
     * Best-first search algorithm
     *
     * @return Plan
     */
    private List<Service> planSearch() {
        YNode n = YNode.builder()
                .state(new State(inputSet))
                .parent(null)
                .steps(Lists.newArrayList())
                .length(0)
                .build();
        YNode n1 = computeNode(n);
        log.debug("Create head node {}", n);

        if (step > bMax)
            return null;
        else if (n1 != null)
            return extractPlan(n1);
        else {
            while (open.size() != 0) {
                YNode node = open.poll();

                for (Service service : node.applicable) {
                    Set<Concept> newNodeConcepts = Sets.newHashSet(n.state.concepts);
                    newNodeConcepts.addAll(service.getOutputConceptSet());
                    YNode newNode = YNode.builder()
                            .state(new State(newNodeConcepts))
                            .parent(node)
                            .steps(Lists.newArrayList(node.steps))
                            .length(node.length + 1)
                            .build();
                    YNode n2 = computeNode(newNode);

                    // Beyond b max
                    if (n2 == null && step > bMax)
                        return null;

                    if (n2 != null) return extractPlan(n2);
                }
            }
            return null;
        }
    }

    /**
     * Extract plan from YNode linked list
     *
     * @param n YNode
     * @return Services from all nodes
     */
    private List<Service> extractPlan(YNode n) {
        if (n == null)
            return null;

        List<Service> res = new LinkedList<>();
        YNode node = n;
        while (node != null) {
            addFront(node.steps, res);
            node = node.parent;
        }
        return res;
    }

    /**
     * Add steps to res head
     *
     * @param steps
     * @param res
     */
    private void addFront(List<Service> steps, List<Service> res) {
        LinkedList<Service> result;
        if (res instanceof LinkedList) {
            result = ((LinkedList<Service>) res);
            for (int i = steps.size() - 1; i >= 0; i--) {
                result.addFirst(steps.get(i));
            }
        }
    }

    /**
     * Generate node
     *
     * @param n Node
     * @return Goal node
     */
    private YNode computeNode(YNode n) {

        if (step > bMax)
            return null;

        for (YNode closedNode : closed) {
            if (closedNode.state.equals(n.state)) return null;
        }

        closed.add(n);
        Pair<Map<NamedObject, Integer>, List<Service>> costApp = computeHAdd(n.state);
        Map<NamedObject, Integer> cost = costApp.getLeft();
        List<Service> app = costApp.getRight();

        int gCost = sumCost(goalSet, cost);

        if (gCost == 0) return n;
        else if (gCost == Integer.MAX_VALUE) return null;
        else {
            n.applicable = app;
            n.heuristic = n.length + w * gCost;
            open.add(n);
            Pair<State, List<Service>> statePlan = lookahead(n.state, cost);
            // Update search step when add new node
            YNode n1 = YNode.builder()
                    .state(statePlan.getLeft())
                    .parent(n)
                    .steps(statePlan.getRight())
                    .length(n.length + statePlan.getRight().size())
                    .build();
            step++;


            log.debug("Create node {}", n1);
            return computeNode(n1);
        }
    }

    /**
     * Compute heuristic value and generate applicable services
     * TODO Cost too manny time
     *
     * @param state State
     * @return NamedObject value map and applicable services
     */
    private Pair<Map<NamedObject, Integer>, List<Service>> computeHAdd(State state) {
        int size = serviceMap.size() + conceptMap.size();
        Map<NamedObject, Integer> cost = new HashMap<>(size);
        Map<NamedObject, Boolean> update = new HashMap<>(size);

        serviceMap.forEach((s, service) -> {
            cost.put(service, Integer.MAX_VALUE);
            update.put(service, service.getInputConceptSet().isEmpty());
        });

        conceptMap.forEach((s, concept) -> {
            if (state.concepts.contains(concept)) {
                cost.put(concept, 0);
                for (Service usedByService : concept.getUsedByServices()) {
                    update.put(usedByService, true);
                }
            } else {
                cost.put(concept, Integer.MAX_VALUE);
            }
        });

        List<Service> app = new LinkedList<>();
        boolean loop = true;

        while (loop) {
            loop = false;

            for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
                Service service = entry.getValue();
                if (update.get(service)) {
                    update.put(service, false);
                    int c = sumCost(service.getInputConceptSet(), cost);
                    if (c < cost.get(service)) {
                        cost.put(service, c);
                        if (c == 0) app.add(service);
                        for (Concept outputConcept : service.getOutputConceptSet()) {
                            if (c + 1 < cost.get(outputConcept)) {
                                cost.put(outputConcept, c + 1);
                                for (Service usedByService : outputConcept.getUsedByServices()) {
                                    loop = true;
                                    update.put(usedByService, true);
                                }
                            }
                        }
                    }
                }
            }

        }
        return new ImmutablePair<>(cost, app);
    }

    /**
     * Sum concepts's cost. If someone of costs is MAX_VALUE, return MAX_VALUE directly
     *
     * @param concepts
     * @param costs
     * @return cost sum
     */
    private int sumCost(Set<Concept> concepts, Map<NamedObject, Integer> costs) {
        int cost = 0;
        for (Concept concept : concepts) {
            int c = costs.get(concept);
            if (c < Integer.MAX_VALUE) {
                cost += c;
            } else {
                cost = Integer.MAX_VALUE;
                break;
            }
        }
        return cost;
    }

    private Pair<State, List<Service>> lookahead(State state, Map<NamedObject, Integer> cost) {
        List<Service> rPlan = extractRelaxedPlan(state, cost);
        List<Service> plan = new ArrayList<>();
        State lookaheadState = state.copy();

        boolean loop = true;
        while (loop && rPlan.size() != 0) {
            loop = false;
            Service service = popMinApplicableService(lookaheadState, rPlan);
            if (service != null) {  // Find available service
                loop = true;
                lookaheadState.concepts.addAll(service.getOutputConceptSet());
                plan.add(service);
            } else {
                log.error("No applicable service");

                int i = 0;
                int j = 0;
                // If no one service was added to plan
                // Use this repair strategy
                while ((!loop) && (i < rPlan.size())) {
                    while (!loop && (j < rPlan.size())) {
                        Service ai = rPlan.get(i);
                        Service aj = rPlan.get(j);

                        if (i != j && !Collections.disjoint(ai.getOutputConceptSet(), aj.getInputConceptSet())) {
                            List<Service> candidates = findCandidates(state, ai, aj);
                            if (candidates.size() != 0) {
                                // Exit repair strategy
                                loop = true;
                                Service a = popMinCandidates(candidates, cost);
                                rPlan.add(i, a);
                            }
                        }
                        j++;
                    }
                    i++;
                }
            }
        }

        return new ImmutablePair<>(lookaheadState, plan);
    }

    private List<Service> findCandidates(State state, Service ai, Service aj) {
        List<Service> candidates = new LinkedList<>();
        for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
            Service service = entry.getValue();
            if (state.concepts.containsAll(service.getInputConceptSet())) {
                Set<Concept> addAiPreAj = Sets.intersection(ai.getOutputConceptSet(), aj.getInputConceptSet());
                if (!Collections.disjoint(addAiPreAj, service.getOutputConceptSet()))
                    candidates.add(service);
            }
        }
        return candidates;
    }

    private Service popMinCandidates(List<Service> candidates, Map<NamedObject, Integer> cost) {
        Service minCostService = candidates.get(0);
        int minCost = cost.get(minCostService);
        for (Service service : candidates) {
            if (cost.get(service) < minCost) {
                minCost = cost.get(service);
                minCostService = service;
            }
        }

        return minCostService;
    }

    private Service popMinApplicableService(State state, List<Service> rPlan) {
        Service service = null;
        Iterator<Service> itr = rPlan.iterator();
        while (itr.hasNext()) {
            Service s = itr.next();
            if (state.concepts.containsAll(s.getInputConceptSet())) {
                service = s;
                itr.remove();
                break;
            }
        }
        return service;
    }

    /**
     * Find a relaxed plan
     *
     * @param state Current state
     * @param cost  Cost cache map
     * @return Plan as linked list
     */
    private List<Service> extractRelaxedPlan(State state, Map<NamedObject, Integer> cost) {
        List<Service> rPlan = new LinkedList<>();
        Queue<Concept> goals = new LinkedList<>(goalSet);
        Set<Concept> satisfied = new HashSet<>(state.concepts);

        while (!goals.isEmpty()) {
            Concept g = goals.poll();
            if (!satisfied.contains(g)) {
                satisfied.add(g);
                Service service = minCostService(g.getProducedByServices(), cost);
                if (!rPlan.contains(service)) {
                    rPlan.add(service);
                    goals.addAll(service.getInputConceptSet());
                    satisfied.addAll(service.getOutputConceptSet());
//                    log.debug("Add required goal {} : {}", service, service.getInputConceptSet());
                }
            }
        }

        // Sort relaxed plan
        rPlan.sort(Comparator.comparing(cost::get));
        log.debug("Find relaxed plan {}", rPlan);
        return rPlan;
    }

    private Service minCostService(Set<Service> usedByServices, Map<NamedObject, Integer> cost) {
        Service minCostService = null;
        int minCost = Integer.MAX_VALUE;
        for (Service service : usedByServices) {
            if (cost.get(service) < minCost) {
                minCost = cost.get(service);
                minCostService = service;
            }
        }
        return minCostService;
    }

}
