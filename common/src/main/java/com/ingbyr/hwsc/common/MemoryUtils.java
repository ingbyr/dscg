package com.ingbyr.hwsc.common;

public class MemoryUtils {
    /**
     * Get current used memory as mb
     * @return Used memory (mb)
     */
    public static long currentUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000;
    }
}
