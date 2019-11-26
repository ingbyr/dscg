package com.ingbyr.hwsc.planner.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author ingbyr
 */
public final class UniformUtils {

    private static final ThreadLocalRandom tlr = ThreadLocalRandom.current();

    /**
     * Returns a random int value
     *
     * @param low  left bound (inclusive)
     * @param high right bound (exclusive)
     * @return int in [low, high)
     */
    public static int rangeIE(int low, int high) {
        return tlr.nextInt(low, high);
    }

    /**
     * Returns a random int value
     *
     * @param low  left bound (inclusive)
     * @param high right bound (inclusive)
     * @return int in [low, high]
     */
    public static int rangeII(int low, int high) {
        if (low == high) return low;
        return tlr.nextInt(low, high + 1);
    }

    /**
     * Returns a random int value
     *
     * @param low  left bound (exclusive)
     * @param high right bound (inclusive)
     * @return int in (low, high]
     */
    public static int rangeEI(int low, int high) {
        return tlr.nextInt(low + 1, high + 1);
    }

    /**
     * Returns a random int value
     *
     * @param low  left bound (exclusive)
     * @param high right bound (exclusive)
     * @return int in (low, high)
     */
    public static int rangeEE(int low, int high) {
        return tlr.nextInt(low + 1, high);
    }

    public static double p() {
        return tlr.nextDouble(0.0, 1.0);
    }

    public static double rangeIE(double low, double high) {
        return tlr.nextDouble(low, high);
    }

    public static int[] indexArray(int collectionSize, int wantedSize) {
        return tlr.ints(0, collectionSize)
                .distinct()
                .limit(wantedSize)
                .boxed()
                .mapToInt(Number::intValue)
                .toArray();
    }

    public static Set<Integer> indexAsSet(int collectionSize, int wantedSize) {
        return tlr.ints(0, collectionSize)
                .distinct()
                .limit(wantedSize)
                .boxed()
                .collect(Collectors.toSet());
    }

    /**
     * Select random size element from set
     *
     * @param set        Data collection
     * @param wantedSize Wanted element size
     * @param <T>        Type
     * @return Random element set
     */
    public static <T> Set<T> set(Set<T> set, int wantedSize) {
        Set<Integer> indexes = indexAsSet(set.size(), wantedSize);
        int i = 0;
        Set<T> res = new HashSet<>(wantedSize);
        for (T item : set) {
            if (indexes.contains(i)) {
                res.add(item);
                if (res.size() == wantedSize) break;
            }
            i++;
        }
        return res;
    }

    public static <T> T oneFromSet(Set<T> set) {
        int index = rangeIE(0, set.size());
        T res = null;
        int i = 0;
        for (T item : set) {
            if (i == index) {
                res = item;
                break;
            }
            i++;
        }
        return res;
    }

    public static <T> T oneFromList(List<T> objs) {
        int selectedIndex = tlr.nextInt(0, objs.size());
        return objs.get(selectedIndex);
    }
}
