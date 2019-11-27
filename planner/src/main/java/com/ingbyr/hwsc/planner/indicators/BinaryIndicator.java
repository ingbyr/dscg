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
    public void calcFitness(List<Individual> population) {
        for (Individual ind : population) {
            ind.setFitness(calculateFitness(ind, population));
        }
    }

    private double calculateFitness(Individual ind, List<Individual> others) {
        double fitness = 0.0;
        for (Individual otherInd : others) {
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
        return Math.exp(-indicatorValue / k);
    }

    private double indicatorValue(Individual ind1, Individual ind2) {
        double distance = indicatorValue(ind1.getQos(), ind2.getQos());
        log.trace("distance {}", distance);
        return indicatorValue(ind1.getQos(), ind2.getQos());
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
