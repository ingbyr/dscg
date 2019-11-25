package com.ingbyr.hwsc.planner.utils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author ingbyr
 */
class UniformUtilsTest {

    @Test
    void uniformInClosedRange() {
        for (int i = 0; i < 10; i++) {
            int randomInt = UniformUtils.rangeII(0, 2);
            assertTrue(randomInt >= 0 && randomInt <= 2);
        }
    }

    @Test
    void uniformIndexes() {
        List<Integer> testCollection = Arrays.asList(3, 1, 2);
        for (int i = 0; i < 10; i++) {
            int[] randomIndexes = UniformUtils.indexArray(testCollection.size(), 2);
            OptionalInt max = Arrays.stream(randomIndexes).max();
            assertTrue(max.getAsInt() < testCollection.size());
        }
    }

    @Test
    void uniformSet() {
        Set<String> strSet = new HashSet<>();
        strSet.add("apple1");
        strSet.add("apple2");
        strSet.add("apple3");
        strSet.add("apple4");
        strSet.add("apple5");

        Set<String> randomStrSet = UniformUtils.set(strSet, 3);
        assertTrue(strSet.containsAll(randomStrSet));
        assertEquals(3, randomStrSet.size());

        String one = UniformUtils.oneFromSet(strSet);
        assertTrue(strSet.contains(one));
    }

}