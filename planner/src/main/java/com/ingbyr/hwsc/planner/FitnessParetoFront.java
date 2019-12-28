package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.QoS;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * Calculate the fitness of individual
 *
 * @author ingbyr
 */

@NoArgsConstructor
@Slf4j
public class FitnessParetoFront implements Fitness {

    @Override
    public void calculatePopulationFitness(List<Individual> population) {

        List<Individual> popCopy = new LinkedList<>(population);
        popCopy.forEach(ind -> ind.setFitness(-1.0));
        double level = 0;
        int cur = popCopy.size();
        int pre = cur + 1;

        while (cur < pre) {
            pre = popCopy.size();
            for (Individual ind : popCopy) {
                boolean next = false;
                for (Individual anotherInd : popCopy) {
                    if (domain(anotherInd, ind)) {
                        next = true;
                        break;
                    }
                }
                if (next) continue;
                ind.setFitness(level);
            }
            popCopy.removeIf(ind -> ind.getFitness() >= 0);
            cur = popCopy.size();
            level++;
        }
        for (Individual ind : popCopy) {
            ind.setFitness(level);
        }

//        population.stream().sorted().forEach(ind -> log.debug("{} {}", ind.getQos().toNumpy(), ind.getFitness()));
    }

    private boolean domain(Individual ind1, Individual ind2) {
        QoS q1 = ind1.getQos();
        QoS q2 = ind2.getQos();
        boolean hasBetter = false;
        for (int type : QoS.TYPES) {
            if (q1.get(type) - q2.get(type) > 0) {
                return false;
            } else if (q1.get(type) - q2.get(type) < 0)
                hasBetter = true;
        }
        return hasBetter;
    }
}
