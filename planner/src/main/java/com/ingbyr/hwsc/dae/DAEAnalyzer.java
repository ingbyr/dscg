package com.ingbyr.hwsc.dae;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ingbyr.hwsc.common.models.Concept;
import com.ingbyr.hwsc.common.models.Qos;
import com.ingbyr.hwsc.common.models.Service;
import com.ingbyr.hwsc.exception.NotValidSolutionException;
import com.ingbyr.hwsc.dataset.reader.DataSetReader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ingbyr
 */
@Slf4j
@RequiredArgsConstructor
public class DAEAnalyzer {

    @NonNull
    private DataSetReader reader;

    // Fitness log
    private final List<Double> fitnessLog = new LinkedList<>();

    // Qos log
    private final List<Qos> qosLog = new LinkedList<>();

    public void addLog(Individual individual) {
        fitnessLog.add(individual.getFitness());
        qosLog.add(individual.getQos());
    }

    public void displayLog() {
        log.info("Process log:");
        Iterator<Double> fitnessItr = fitnessLog.iterator();
        Iterator<Qos> qosItr = qosLog.iterator();
        int step = 0;
        while (fitnessItr.hasNext() && qosItr.hasNext()) {
            log.debug("[{}] Fitness {}, {}", step++, fitnessItr.next(), qosItr.next());
        }
    }

    public void transToUIData() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        List<Object> data = new ArrayList<>(qosLog.size() + 1);
        String[] qosTypes = new String[Qos.names.length + 1];
        System.arraycopy(Qos.names, 0, qosTypes, 1, Qos.names.length);
        qosTypes[0] = "Step";
        data.add(qosTypes);

        int qosNum = Qos.names.length;
        // Add step to qos log
        for (int i = 0; i < qosLog.size(); i++) {
            double[] qosWithStep = new double[qosNum + 1];
            System.arraycopy(qosLog.get(i).getValues(), 0, qosWithStep, 1, qosNum);
            qosWithStep[0] = i;
            data.add(qosWithStep);
        }

        writer.writeValue(new File(".//log//qos.json"), data);
    }

    public static void displayPopulation(List<Individual> individuals) {
        log.debug("Current population:");
        individuals.forEach(individual -> {
            Integer id = individual.getId();
            log.debug("[{}] Individual {}", id, individual);
            log.debug("[{}] Services {}", id, individual.getServices());
            log.debug("[{}] Fitness {}, {}", id, individual.getFitness(), individual.getQos());
            log.debug("");
        });
    }

    public static void checkSolution(Set<Concept> input, Set<Concept> goal, List<Service> services) throws NotValidSolutionException {
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
