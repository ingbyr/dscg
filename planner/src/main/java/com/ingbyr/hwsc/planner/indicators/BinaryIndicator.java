package com.ingbyr.hwsc.planner.indicators;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.planner.Individual;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Calculate the fitness of individual
 *
 * @author ingbyr
 */

@AllArgsConstructor
@Slf4j
public class BinaryIndicator implements Indicator {

    private double k;

    @Override
    public void calculatePopulationFitness(List<Individual> population) {
        for (Individual ind : population) {
            ind.setFitness(calculateIndividualFitness(ind, population));
        }
    }

    private double calculateIndividualFitness(Individual ind, List<Individual> population) {
        double fitness = 0.0;
        for (Individual otherInd : population) {
            // Skip itself
            if (otherInd.equals(ind))
                continue;
            log.trace("Ind {}, other ind {}", ind.getId(), otherInd.getId());
            fitness += toPartFitness(indicatorValue(otherInd, ind));
        }
        log.debug("Ind {} final fitness {}", ind.getId(), fitness);
        return fitness;
    }

    private double toPartFitness(double indicatorValue) {
        double fitness = Math.exp(-indicatorValue / k);
        log.trace("Part fitness {}", fitness);
        return fitness;
    }

    private double indicatorValue(Individual ind1, Individual ind2) {
        double distance = indicatorValue(ind1.getQos(), ind2.getQos());
        log.trace("Distance {}", distance);
        return distance;
    }

    private double indicatorValue(Qos qos1, Qos qos2) {
        double minDistance = Double.MAX_VALUE;
        double maxDistance = Double.MIN_EXPONENT;
        boolean isPositive = false;
        for (int type : Qos.TYPES) {
            double distance = qos1.get(type) - qos2.get(type);
            if (distance >= 0) {
                isPositive = true;
                minDistance = Math.min(minDistance, distance);
            }
            if (!isPositive)
                maxDistance = Math.max(maxDistance, distance);
        }

        return isPositive ? minDistance : maxDistance;
    }
}
