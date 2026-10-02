package com.frapee;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Foundation guide to Java's standard functional interfaces.
 * Use this class to compare transformations, predicates, suppliers, and actions.
 */
public class FoundationFunctional {

    /**
     * Apply a one-input transformation.
     * @param value input value
     * @param function transformation to apply
     * @return transformed value
     */
    public int applyFunction(int value, Function<Integer, Integer> function) {
        return function.apply(value);
    }

    /**
     * Apply a two-input transformation.
     * @param left first input
     * @param right second input
     * @param function transformation to apply
     * @return transformed value
     */
    public int applyBiFunction(int left, int right,
            BiFunction<Integer, Integer, Integer> function) {
        return function.apply(left, right);
    }

    /**
     * Evaluate a condition supplied as a Predicate.
     * @param value input value
     * @param predicate condition to evaluate
     * @return predicate result
     */
    public boolean testPredicate(int value, Predicate<Integer> predicate) {
        return predicate.test(value);
    }

    /**
     * Produce a value on demand with a Supplier.
     * @param supplier deferred value provider
     * @return supplied value
     */
    public int supplyValue(Supplier<Integer> supplier) {
        return supplier.get();
    }

    /**
     * Perform an action with a Consumer.
     * @param value value passed to the consumer
     * @param consumer action to invoke
     */
    public void acceptNumber(int value, Consumer<Integer> consumer) {
        consumer.accept(value);
    }

    /**
     * Perform an action once for each value.
     * @param values values passed one at a time
     * @param consumer action invoked for each value
     */
    public void forEachNumber(List<Integer> values, Consumer<Integer> consumer) {
        values.forEach(consumer);
    }

    /**
     * Execute a Consumer and convert a runtime failure to a status.
     * @param value value passed to the consumer
     * @param consumer action to invoke
     * @return false when the consumer throws a RuntimeException
     */
    public boolean runSafely(int value, Consumer<Integer> consumer) {
        try {
            consumer.accept(value);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
