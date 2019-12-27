package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.util.WorkDir;
import com.ingbyr.hwsc.dataset.Dataset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

/**
 * File name daex.properties
 *
 * @author ingbyr
 */
@Slf4j
public final class PlannerConfigFile extends PlannerConfig {

    private static final File CONFIG_FILE = WorkDir.WORK_DIR.resolve("planner.properties").toFile();

    private static final String DATASET = "dataset";

    private static final String POPULATION_SIZE = "population_size";

    private static final String OFFSPRING_SIZE = "offspring_size";

    private static final String SURVIVAL_SIZE = "survival_size";

    private static final String P_CROSS = "p_cross";

    private static final String P_MUT = "p_mut";

    private static final String MUTATION_ADD_STATE_WEIGHT = "mutation_add_state_weight";

    private static final String MUTATION_ADD_CONCEPT_WEIGHT = "mutation_add_concept_weight";

    private static final String MUTATION_DEL_STATE_WEIGHT = "mutation_del_state_weight";

    private static final String MUTATION_DEL_CONCEPT_WEIGHT = "mutation_del_concept_weight";

    private static final String ENABLE_AUTO_STOP = "enable_auto_stop";

    private static final String MAX_GEN = "max_gen";

    private static final String STOP_STEP = "stop_step";

    private static final String SAVE_TO_FILE = "save_to_file";

    private static final String MUTATION_ADD_STATE_RADIUS = "mutation_add_state_radius";

    public static final String MUTATION_ADD_CONCEPT_ADD_POSSIBILITY = "mutation_add_concept_add_possibility";

    public static final String MUTATION_ADD_CONCEPT_CHANGE_POSSIBILITY = "mutation_add_concept_change_possibility";

    public static final String EVALUATOR = "evaluator";

    public static final String FITNESS = "fitness";

    public static final String INNER_PLAN_MAX_STEP = "inner_plan_max_step";

    public static final String MAX_STATE_SIZE = "max_state_size";

    /**
     * Load planner config from planner.properties file
     *
     * @throws ConfigurationException
     */
    public PlannerConfigFile() throws ConfigurationException {

        Configurations configHelp = new Configurations();
        log.info("Config file {}", CONFIG_FILE.getAbsolutePath());
        Configuration config = configHelp.properties(CONFIG_FILE);

        dataset = Dataset.valueOf(config.getString(DATASET));
        populationSize = config.getInt(POPULATION_SIZE);
        offspringSize = config.getInt(OFFSPRING_SIZE);
        survivalSize = config.getInt(SURVIVAL_SIZE);
        crossoverPossibility = config.getDouble(P_CROSS);
        mutationPossibility = config.getDouble(P_MUT);
        mutationAddStateWeight = config.getInt(MUTATION_ADD_STATE_WEIGHT);
        mutationAddConceptWeight = config.getInt(MUTATION_ADD_CONCEPT_WEIGHT);
        mutationDelStateWeight = config.getInt(MUTATION_DEL_STATE_WEIGHT);
        mutationDelConceptWeight = config.getInt(MUTATION_DEL_CONCEPT_WEIGHT);
        enableAutoStop = config.getBoolean(ENABLE_AUTO_STOP);
        maxGen = config.getInt(MAX_GEN);
        autoStopStep = config.getInt(STOP_STEP);
        saveToFile = config.getBoolean(SAVE_TO_FILE);
        mutationAddStateRadius = config.getInt(MUTATION_ADD_STATE_RADIUS);
        mutationAddConceptAddPossibility = config.getDouble(MUTATION_ADD_CONCEPT_ADD_POSSIBILITY);
        mutationAddConceptChangePossibility = config.getDouble(MUTATION_ADD_CONCEPT_CHANGE_POSSIBILITY);
        evaluator = config.getString(EVALUATOR);
        fitness = config.getString(FITNESS);
        innerPlanMaxStep = config.getInt(INNER_PLAN_MAX_STEP);
        maxStateSize = config.getInt(MAX_STATE_SIZE);
    }
}
