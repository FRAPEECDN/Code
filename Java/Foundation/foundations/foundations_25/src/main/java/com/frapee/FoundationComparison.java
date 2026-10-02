package com.frapee;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Foundation guide to ordering values with Comparable and Comparator.
 * Natural ordering belongs to a value type; Comparator supplies an external
 * ordering strategy and can chain multiple criteria.
 */
public class FoundationComparison {

    /**
     * Compare two integers using their natural ordering.
     * @param left first value
     * @param right second value
     * @return negative, zero, or positive according to the comparison
     */
    public int compareIntegers(int left, int right) {
        return Integer.compare(left, right);
    }

    /**
     * Sort strings using their natural Comparable ordering.
     * @param values values to sort
     * @return sorted copy of the values
     */
    public List<String> sortNaturally(List<String> values) {
        List<String> sortedValues = new ArrayList<>(values);
        sortedValues.sort(Comparable::compareTo);
        return sortedValues;
    }

    /**
     * Sort strings by length using a Comparator.
     * @param values values to sort
     * @return sorted copy, shortest values first
     */
    public List<String> sortByLength(List<String> values) {
        List<String> sortedValues = new ArrayList<>(values);
        sortedValues.sort(Comparator.comparingInt(String::length));
        return sortedValues;
    }

    /**
     * Sort strings by length and then alphabetically.
     * @param values values to sort
     * @return sorted copy using two comparator criteria
     */
    public List<String> sortByLengthThenName(List<String> values) {
        List<String> sortedValues = new ArrayList<>(values);
        Comparator<String> comparator = Comparator.comparingInt(String::length)
            .thenComparing(Comparator.naturalOrder());
        sortedValues.sort(comparator);
        return sortedValues;
    }

    /**
     * Compare two values with a caller-provided Comparator.
     * @param left first value
     * @param right second value
     * @param comparator comparison strategy
     * @return comparator result
     */
    public int compareWith(int left, int right, Comparator<Integer> comparator) {
        return comparator.compare(left, right);
    }
}