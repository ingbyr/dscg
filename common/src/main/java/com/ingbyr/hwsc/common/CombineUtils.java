package com.ingbyr.hwsc.common;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CombineUtils {

    public static <T> Set<Set<T>> combine(List<List<T>> itemList) {
        Set<Set<T>> combinationResult = new LinkedHashSet<>();
        List<T> combinationStepResult = new LinkedList<>();
        combineHelper(itemList, 0, combinationResult, combinationStepResult);
        return combinationResult;
    }

    private static <T> void combineHelper(List<List<T>> itemList,
                                         int depth,
                                         Set<Set<T>> labelCombinationResult,
                                         List<T> labelCombinationStepResult) {

        // FIXME auto stop combine service when size too big
        if (labelCombinationResult.size() > 5)
            return;

        for (int i = 0; i < itemList.get(depth).size(); i++) {
            T item = itemList.get(depth).get(i);
            try {
                labelCombinationStepResult.set(depth, item);
            } catch (IndexOutOfBoundsException e) {
                labelCombinationStepResult.add(item);
            }

            if (depth == itemList.size() - 1) {
                // create new one because that data will be reset in next search
                labelCombinationResult.add(new LinkedHashSet<>(labelCombinationStepResult));
            } else {
                combineHelper(itemList, depth + 1, labelCombinationResult, labelCombinationStepResult);
            }
        }
    }
}
