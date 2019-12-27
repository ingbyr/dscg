package com.ingbyr.hwsc.planner.indicators;

import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.planner.FitnessBinaryIndicator;
import com.ingbyr.hwsc.planner.Fitness;
import com.ingbyr.hwsc.planner.Individual;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ingbyr
 */
class FitnessBinaryIndicatorTest {

    @Test
    void calculateBinaryIndicator() {
        List<Individual> pop = new ArrayList<>();
        Individual i1 = new Individual();
        Qos q1 = new Qos();
        q1.setValues(new double[]{6.21775225805416, 4.393617021276596, 2.8510638297872344, 3.800000000000001, 4.64763134571966, 4.0160320641282565});
        i1.setQos(q1);

        Individual i2 = new Individual();
        Qos q2 = new Qos();
        q2.setValues(new double[]{3.3382185948904373, 3.4361702127659575, 1.6276595744680853, 2.90909090909091, 2.4080249116742394, 3.040080160320641});
        i2.setQos(q2);

        Individual i3 = new Individual();
        Qos q3 = new Qos();
        q3.setValues(new double[]{3.1785818898238096, 3.159574468085107, 2.3085106382978724, 2.145454545454546, 1.8270318935198369, 2.514028056112224});
        i3.setQos(q3);

        Individual i4 = new Individual();
        Qos q4 = new Qos();
        q4.setValues(new double[]{5.031249623824828, 5.989361702127661, 3.851063829787234, 5.7272727272727275, 3.2696151362883534, 5.367735470941883});
        i4.setQos(q4);

        pop.add(i1);
        pop.add(i2);
        pop.add(i3);
        pop.add(i4);

        Fitness fitness =new FitnessBinaryIndicator();
        fitness.calculatePopulationFitness(pop);
        for (Individual individual : pop) {
            System.out.println(individual.getFitness() +" " + Arrays.toString(individual.getQos().getValues()));
        }
    }

}