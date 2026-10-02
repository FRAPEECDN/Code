package com.frapee;

import java.util.concurrent.StructuredTaskScope;

/**
 * Java 25 preview example for joining related virtual-thread tasks as one unit.
 */
public class FoundationStructuredConcurrency {

    /**
     * Calculate two factorials concurrently and combine their results.
     * @param left first factorial input
     * @param right second factorial input
     * @return sum of both factorial results
     * @throws InterruptedException if the calling thread is interrupted
     */
    public long combineFactorials(int left, int right) throws InterruptedException {
        validateFactorialInput(left);
        validateFactorialInput(right);
        try (var scope = StructuredTaskScope.open()) {
            var leftResult = scope.fork(() -> factorial(left));
            var rightResult = scope.fork(() -> factorial(right));
            scope.join();
            return Math.addExact(leftResult.get(), rightResult.get());
        }
    }

    private long factorial(int number) {
        long result = 1;
        for (int value = number; value > 0; value--) {
            result = Math.multiplyExact(result, value);
        }
        return result;
    }

    private void validateFactorialInput(int number) {
        if (number < 0 || number > 20) {
            throw new IllegalArgumentException("Factorial input must be between 0 and 20");
        }
    }
}