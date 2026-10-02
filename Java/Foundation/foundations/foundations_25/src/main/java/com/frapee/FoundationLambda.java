package com.frapee;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Foundation examples for lambda expressions and functional composition.
 */
public class FoundationLambda {

    /**
     * Apply a lambda transformation to every value.
     * @param values input values
     * @param mapper transformation lambda
     * @return transformed values
     */
    public List<Integer> mapValues(List<Integer> values, Function<Integer, Integer> mapper) {
        return values.stream().map(mapper).toList();
    }

    /**
     * Keep values that satisfy a lambda predicate.
     * @param values input values
     * @param predicate filtering lambda
     * @return matching values
     */
    public List<Integer> filterValues(List<Integer> values, Predicate<Integer> predicate) {
        return values.stream().filter(predicate).toList();
    }

    /**
     * Reduce values using a binary lambda operation.
     * @param values input values
     * @param initialValue starting accumulator value
     * @param operation reduction lambda
     * @return reduced value
     */
    public int reduceValues(List<Integer> values, int initialValue,
            BiFunction<Integer, Integer, Integer> operation) {
        int result = initialValue;
        for (int value : values) {
            result = operation.apply(result, value);
        }
        return result;
    }

    /**
     * Compose two Function lambdas so the first result becomes the second input.
     * @param first first transformation
     * @param second second transformation
     * @param value initial input
     * @return composed result
     */
    public int composeFunctions(Function<Integer, Integer> first,
            Function<Integer, Integer> second, int value) {
        return second.compose(first).apply(value);
    }

    /**
     * Demonstrate a lambda capturing an effectively final local value.
     * @param amount value captured by the returned lambda
     * @return function that adds amount to its input
     */
    public Function<Integer, Integer> createAdder(int amount) {
        return value -> value + amount;
    }

    /**
     * Join values after transforming each one with a lambda.
     * @param values input values
     * @param formatter formatting lambda
     * @return comma-separated formatted values
     */
    public String joinFormatted(List<Integer> values, Function<Integer, String> formatter) {
        return values.stream().map(formatter).collect(Collectors.joining(","));
    }
}