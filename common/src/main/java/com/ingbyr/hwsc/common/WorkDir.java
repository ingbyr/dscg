package com.ingbyr.hwsc.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Must define the work dir (HWSC_WORK_DIR) before launch IDE or run jar file
 */
public final class WorkDir {

    public static final Path WORK_DIR = Paths.get(System.getenv("HWSC_WORK_DIR"));

    public static final Path DATASET_DIR = WORK_DIR.resolve("dataset");

    public static final Path LOG_DIR = WORK_DIR.resolve("log");

    public static final Path RESULT_DIR = WORK_DIR.resolve("result");

    public static final Path BENCH_RESULT_DIR = RESULT_DIR.resolve("bench");

    public static final Path PLANNER_LOG_DIR = RESULT_DIR.resolve("planner");

    private static final String ACTIVE_QOS = Qos.TYPES_STRING;

    public static final Path QOS_SP_DIR = RESULT_DIR.resolve(ACTIVE_QOS).resolve("sp");

    public static final Path QOS_PF_DIR = RESULT_DIR.resolve(ACTIVE_QOS).resolve("pf");

    // Create result dirs
    static {
        createDirIfNotExist(QOS_SP_DIR);
        createDirIfNotExist(QOS_PF_DIR);
        createDirIfNotExist(BENCH_RESULT_DIR);
        createDirIfNotExist(PLANNER_LOG_DIR);
    }

    private static void createDirIfNotExist(Path dir) {
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Path getSearchSpaceFile(String dataset) {
        return WorkDir.QOS_SP_DIR.resolve(dataset + ".txt");
    }

    public static Path getRawSearchSpaceFile(String dataset) {
        return WorkDir.QOS_SP_DIR.resolve(dataset + "_raw.txt");
    }

    public static Path getParetoFrontFile(String dataset) {
        return WorkDir.QOS_PF_DIR.resolve(dataset + ".txt");
    }

    public static Path getRawParetoFrontFile(String dataset) {
        return WorkDir.QOS_PF_DIR.resolve(dataset + "_raw.txt");
    }

    public static Path getPlannerBenchFile(String dataset) {
        return BENCH_RESULT_DIR.resolve(dataset + ".json");
    }
}
