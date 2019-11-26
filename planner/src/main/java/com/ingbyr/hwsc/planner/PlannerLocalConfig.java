package com.ingbyr.hwsc.planner;

import com.ingbyr.hwsc.common.util.FileUtils;
import com.ingbyr.hwsc.dataset.Dataset;
import lombok.ToString;
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
@ToString
@Slf4j
public final class PlannerLocalConfig extends AbstractPlannerConfig {

    private static final File CONFIG_FILE = FileUtils.CURRENT_DIR.resolve("planner.properties").toFile();

    private static final String DATASET = "dataset";

    private static final String POPULATION_SIZE = "population_size";

    private static final String OFFSPRING_SIZE = "offspring_size";

    private static final String SURVIVAL_SIZE = "survival_size";

    private static final String P_CROSS = "p_cross";

    private static final String P_MUT = "p_mut";

    private static final String MUTATION_ADD_STATE_WEIGHT = "mutation_add_goal_weight";

    private static final String MUTATION_ADD_CONCEPT_WEIGHT = "mutation_add_atom_weight";

    private static final String MUTATION_DEL_STATE_WEIGHT = "mutation_del_goal_weight";

    private static final String MUTATION_DEL_CONCEPT_WEIGHT = "mutation_del_atom_weight";

    private static final String ENABLE_CONCURRENT = "enable_concurrent";

    private static final String ENABLE_AUTO_STOP = "enable_auto_stop";

    private static final String MAX_GEN = "max_gen";

    private static final String STOP_STEP = "stop_step";

    /**
     * Load planner config from planner.properties file
     * @throws ConfigurationException
     */
    public PlannerLocalConfig() throws ConfigurationException {

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
        enableConcurrentMode = config.getBoolean(ENABLE_CONCURRENT);
        enableAutoStop = config.getBoolean(ENABLE_AUTO_STOP);
        maxGen = config.getInt(MAX_GEN);
        autoStopStep = config.getInt(STOP_STEP);
    }
}
