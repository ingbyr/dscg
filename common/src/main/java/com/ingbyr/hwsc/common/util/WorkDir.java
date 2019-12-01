package com.ingbyr.hwsc.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class WorkDir {

    /**
     * Must define the work dir (HWSC_WORK_DIR) before launch IDE or run jar file
     */
    public static final Path WORK_DIR = Paths.get(System.getenv("HWSC_WORK_DIR"));

    public static final Path LOG_DIR = WORK_DIR.resolve("log");

    public static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

}
