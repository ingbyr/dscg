package com.hwsc.bench;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.common.XmlDatasetReader;
import com.ingbyr.hwsc.graphplan.qgp.BeamTPG;
import com.ingbyr.hwsc.graphplan.qgp.QPG;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    @Parameter(names = {"-type", "-t"})
    String type;

    @Parameter(names = {"-bench", "-b"})
    int bench;

    @Parameter(names = {"-maxPreNode", "-mn"})
    int maxPreNode;

    @Parameter(names = {"-dataset", "-d"})
    String datasetName;

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    public void run() {
        DataSetReader reader = new XmlDatasetReader();

        try {
            Dataset dataset = Dataset.valueOf(datasetName);
            switch (type) {
                case "sp:cpg":
                    SearchSpace.findByCPG(dataset, maxPreNode);
                    break;
                case "sp:hwsc":
                    SearchSpace.findByHWSC(dataset, bench);
                    break;
                case "dj":
                    SearchSpace.findBestQoS(dataset, maxPreNode);
                    break;
                case "pf":
                    ParetoFront.find(dataset);
                    break;
                case "tpg":
                    reader.setDataset(dataset);
                    BeamTPG tpg = new BeamTPG(reader);
                    BeamTPG.BEAM_WIDTH = maxPreNode;
                    tpg.beamSearch();
                    break;
                default:
                    displayHelpInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayHelpInfo();
        }
    }

    private static void displayHelpInfo() {
        log.info("[-type, -t] sp:cpg (search space)");
        log.info("[-type, -t] sp:hwsc (best result by dijkstra)");
        log.info("[-type, -t] pf (pareto front)");
        log.info("[-bench, -b: bench size");
        log.info("[-dataset, -d] dataset ("
                + Arrays.stream(Dataset.values()).map(Enum::name).collect(Collectors.joining(",")) + ")");
        log.info("[-maxPreNode, -mn] max size of new pre node");
    }
}
