package com.ingbyr.hwsc.dae;

import com.ingbyr.hwsc.dataset.Dataset;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

/**
 * File name daex.properties
 * <p>
 * population_size = 50
 * offspring_size = 200
 * survival_size = 5
 * p_cross = 0.2
 * p_mut = 0.8
 * max_gen = 30
 *
 * @author ingbyr
 */
@ToString
@Slf4j
public final class DAEXConfig {

    private static final String CONFIG_PATH = "planner//daex.properties";

    private static final String DATASET = "dataset";
    Dataset dataset;

    private static final String POPULATION_SIZE = "population_size";
    int populationSize;

    private static final String OFFSPRING_SIZE = "offspring_size";
    int offspringSize;

    private static final String SURVIVAL_SIZE = "survival_size";
    int survivalSize;

    private static final String P_CROSS = "p_cross";
    double pCross;

    private static final String P_MUT = "p_mut";
    double pMut;

    private static final String MUTATION_ADD_GOAL_WEIGHT = "mutation_add_goal_weight";
    int mutationAddGoalWeight;

    private static final String MUTATION_ADD_ATOM_WEIGHT = "mutation_add_atom_weight";
    int mutationAddAtomWeight;

    private static final String MUTATION_DEL_GOAL_WEIGHT = "mutation_del_goal_weight";
    int mutationDelGoalWeight;

    private static final String MUTATION_DEL_ATOM_WEIGHT = "mutation_del_atom_weight";
    int mutationDelAtomWeight;

    private static final String ENABLE_CONCURRENT = "enable_concurrent";
    boolean enableConcurrent;

    private static final String ENABLE_AUTO_STOP = "enable_auto_stop";
    boolean enableAutoStop;

    private static final String MAX_GEN = "max_gen";
    int maxGen;

    private static final String STOP_STEP = "stop_step";
    int stopStep;

    public DAEXConfig() throws ConfigurationException {

        Configurations configHelp = new Configurations();
        File file = new File(CONFIG_PATH);
        log.info("Config file {}", file.getAbsolutePath());

        Configuration config = configHelp.properties(new File(CONFIG_PATH));

        dataset = Dataset.valueOf(config.getString(DATASET));
        populationSize = config.getInt(POPULATION_SIZE);
        offspringSize = config.getInt(OFFSPRING_SIZE);
        survivalSize = config.getInt(SURVIVAL_SIZE);
        pCross = config.getDouble(P_CROSS);
        pMut = config.getDouble(P_MUT);
        mutationAddGoalWeight = config.getInt(MUTATION_ADD_GOAL_WEIGHT);
        mutationAddAtomWeight = config.getInt(MUTATION_ADD_ATOM_WEIGHT);
        mutationDelGoalWeight = config.getInt(MUTATION_DEL_GOAL_WEIGHT);
        mutationDelAtomWeight = config.getInt(MUTATION_DEL_ATOM_WEIGHT);
        enableConcurrent = config.getBoolean(ENABLE_CONCURRENT);
        enableAutoStop = config.getBoolean(ENABLE_AUTO_STOP);
        maxGen = config.getInt(MAX_GEN);
        stopStep = config.getInt(STOP_STEP);
    }
}
