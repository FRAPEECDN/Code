package com.frapee;

/**
 * Runnable helper that calculates a bounded factorial and stores its result.
 * Used by the platform-thread and virtual-thread Foundations.
 */
public class ThreadReturnable implements Runnable {

    private volatile long value = 0;
    private long number = 0;

    /**
     * Factorial function with simple loop that will be run a sync
     * @param number input number being calculated
     * @return factorial for the number
     */
    private long factorial(long number) {
        if (number < 0 || number > 20) {
            throw new IllegalArgumentException("Factorial input must be between 0 and 20");
        }
        long result = 1;
        for (long i = number; i > 0; i--) {
            result = Math.multiplyExact(result, i);
        }
        return result;
    }

    /** Run the factorial calculation on the configured number. */
    @Override
    public void run() {
        value = factorial(this.number);
    }

    /** @return calculated factorial value */
    public long getValue() {
        return value;
    }

    /** @param number factorial input, from 0 through 20 */
    public void setNumber(long number) {
        this.number = number;
    }

}
