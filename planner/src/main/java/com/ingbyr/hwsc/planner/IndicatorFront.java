package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.models.Qos;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Calculate the fitness of individual
 *
 * @author ingbyr
 */

@NoArgsConstructor
@Slf4j
public class IndicatorFront implements Indicator {

    @Override
    public void calculatePopulationFitness(List<Individual> population) {
        for (Individual ind : population) {
            ind.setFitness(calculateIndividualFitness(ind, population));
        }
    }

    private double calculateIndividualFitness(Individual ind, List<Individual> population) {
        double fitness = 0;
        for (Individual otherInd : population) {
            log.trace("Ind {}, other ind {}", ind.getId(), otherInd.getId());
            if (domain(otherInd, ind)) {
                fitness += 1;
            }
        }
        log.debug("Ind {} front fitness {}", ind.getQos(), fitness);
        return fitness;
    }


    private boolean domain(Individual ind1, Individual ind2) {
        Qos qos1 = ind1.getQos();
        Qos qos2 = ind2.getQos();

        for (int type : Qos.ACTIVE_TYPES) {
            if (qos1.get(type) - qos2.get(type) >= 0)
                return false;
        }

        return true;
    }
}
