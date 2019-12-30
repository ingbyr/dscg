package com.ingbyr.hwsc.planner.innerplanner;

import com.ingbyr.hwsc.common.Service;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ingbyr
 */
@AllArgsConstructor
@EqualsAndHashCode
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
}
