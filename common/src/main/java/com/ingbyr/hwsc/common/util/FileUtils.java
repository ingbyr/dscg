package com.ingbyr.hwsc.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {

    public static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir")).getParent();

}
