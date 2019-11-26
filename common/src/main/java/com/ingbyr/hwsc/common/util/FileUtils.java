package com.ingbyr.hwsc.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {

    /**
     * Must define the work dir (HWSC_WORK_DIR) before launch IDE or run jar file
     */
    public static final Path WORK_DIR = Paths.get(System.getenv("HWSC_WORK_DIR"));

}
