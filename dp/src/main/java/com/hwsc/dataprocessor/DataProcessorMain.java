package com.hwsc.dataprocessor;

import com.ingbyr.hwsc.common.Qos;
import com.ingbyr.hwsc.common.Dataset;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class DataProcessorMain {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            displayHelpInfo();
            return;
        }
        Dataset dataset = Dataset.valueOf(args[1]);
        log.info("Active qos: {}", Arrays.toString(Qos.NAMES));
        switch (args[0]) {
            case "sp":
                CompleteGraph.findSearchSpace(dataset);
                break;
            case "pf":
                ParetoFront.find(dataset);
                break;
            default:
                displayHelpInfo();
        }
    }

    private static void displayHelpInfo() {
        System.out.println("arg[0]: sp (search space)");
        System.out.println("arg[0]: pf (pareto front)");
        System.out.println("arg[1]: dataset " + Arrays.toString(Dataset.values()));
    }
}
