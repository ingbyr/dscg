package com.ingbyr.hwsc.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {

    private static final String PROPERTY_USER_DIR = "user.dir";

    public static final Path PROJECT_DIR = Paths.get(System.getProperty(PROPERTY_USER_DIR)).getParent();
}
