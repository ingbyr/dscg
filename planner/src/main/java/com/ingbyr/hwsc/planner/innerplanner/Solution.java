package com.ingbyr.hwsc.planner.innerplanner;

import com.ingbyr.hwsc.common.Concept;
import com.ingbyr.hwsc.common.Service;
import com.ingbyr.hwsc.planner.exception.NotValidSolutionException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ingbyr
 */
@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public class Solution {

    public List<Service> services;

    /**
     * Search cost
     */
    @EqualsAndHashCode.Exclude
    public double searchCost;

    @Override
    public String toString() {
        return "Solution{" +
                "services@" + (services == null ? null : services.hashCode()) +
                ", searchCost=" + searchCost +
                '}';
    }

    double getCost() {
        if (services == null) return -1;
        return services.stream().mapToDouble(Service::getCost).sum();
    }

    public static void check(Set<Concept> input, Set<Concept> goal, List<Service> services) throws NotValidSolutionException {
        if (services == null) {
            log.error("Service list is null");
            return;
        }

        Set<Concept> concepts = new HashSet<>(input);
        for (Service service : services) {
            if (!concepts.containsAll(service.getInputConceptSet()))
                throw new NotValidSolutionException("Service " + service + " can not proceed because that some input concepts not existed");
            concepts.addAll(service.getOutputConceptSet());
        }

        if (!concepts.containsAll(goal))
            throw new NotValidSolutionException("Some goals are not contained when finishing execution");

        log.debug("The solution is valid");
    }
}
