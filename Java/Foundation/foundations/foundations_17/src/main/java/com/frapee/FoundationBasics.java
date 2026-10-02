package com.frapee;

/**
 * Foundation guide to core Java language control flow.
 * Use this class as a quick reference for parameters, conditionals, operators,
 * switch expressions, loops, exceptions, recursion, and overloads.
 */
public class FoundationBasics {

    private static int CHANGE_VAL = 2;

    /**
     * Java only allows parameters to be pass by value.
     * expectation is caller value will not change
     * @param input - for pass by value test
     */
    public void passByValueOnly(int input) {
        input *= CHANGE_VAL;
    }

    /**
     * Java will modify returned value
     * @param input - pass by value input
     * @return modified value
     */
    public int returnChanged(int input) {
        return input * CHANGE_VAL;
    }

    /**
     * Java If statement example
     * @param left - value for comparison in if
     * @param right - value for comparison in if
     * @return test results
     */
    public int ifOnly(int left, int right) {
        if (left < right) {
            return -1;
        }
        return 0; // Note function required to return something
    }

    /**
     *  Java If else statement example
     * @param left - value for comparison in if
     * @param right - value for comparison in if
     * @return test results
     */
    public int ifAndElse(int left, int right) {
        if (left < right) {
            return -1; 
        } else {
            return 1;
        }
    }

    /**
     * Java If, Else If, Else statement example
     * @param left - value for comparison in if
     * @param right - value for comparison in if
     * @return test results
     */
    public int ifMulti(int left, int right) {
        if (left < right) {
            return -1;
        } else if (left > right) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Conditional expression example, also known as the ternary operator.
     * @param left - value for comparison
     * @param right - value for comparison
     * @return -1 when left is smaller, 1 when left is larger, otherwise 0
     */
    public int ternary(int left, int right) {
        return left < right ? -1 : left > right ? 1 : 0;
    }

    /**
     * Logical AND operator example.
     * @param left - first boolean value
     * @param right - second boolean value
     * @return true only when both values are true
     */
    public boolean logicalAnd(boolean left, boolean right) {
        return left && right;
    }

    /**
     * Logical OR operator example.
     * @param left - first boolean value
     * @param right - second boolean value
     * @return true when either value is true
     */
    public boolean logicalOr(boolean left, boolean right) {
        return left || right;
    }

    /**
     * Logical exclusive OR operator example.
     * @param left - first boolean value
     * @param right - second boolean value
     * @return true when exactly one value is true
     */
    public boolean logicalXor(boolean left, boolean right) {
        return left ^ right;
    }

    /**
     * Logical NOT operator example.
     * @param value - boolean value to invert
     * @return the opposite boolean value
     */
    public boolean logicalNot(boolean value) {
        return !value;
    }

    /**
     * Switch statement example (old style)
     * @param option - option on which switch will run
     * @return some value to test switch
     */
    public int switchOlder(int option) {
        int returnVal = 0;
        switch (option) {
            case 0:
                returnVal = 0;
                break;
            case 1:
                returnVal = 1;
                break;
            default:
                returnVal = -1;
                break;
        }
        return returnVal;
    }

    /**
     * Switch statement can handle Strings by default
     * @param option - option on which switch will run
     * @return some value to test switch
     */
    public int switchString(String option) {
        int returnVal = 0;
        switch (option) {
            case "A":
                returnVal = 0;
                break;
            case "B":
                returnVal = 1;
                break;
        
            default:
                returnVal = -1;
                break;
        }
        return returnVal;
    }

    /**
     * Switch statement example (Java 17 and later).
     * Can return directly on options
     * @param option - option on which switch will run
     * @return some value to test switch
     */
    public int switchNew(int option) {
        return switch (option) {
            case 0 -> 0;
            case 1 -> 1;
            default -> -1;
        };
    }

    /**
     * Switch statement example (Java 17 and later) using yield from a block.
     * @param option - option on which switch will run
     * @return some value to test switch
     */
    public int switchYield(int option) {
        return switch (option) {
            case 0 -> {
                yield 0;
            }
            case 1 -> {
                yield 1;
            }
            default -> {
                yield -1;
            }
        };
    }

    /**
     * Break statement example. The loop stops before the requested index.
     * @param noLoops - maximum number of loop iterations
     * @param stopAt - iteration at which the loop should stop
     * @return number of completed iterations
     */
    public int breakLoop(int noLoops, int stopAt) {
        int completedLoops = 0;
        for (int iterateValue = 0; iterateValue < noLoops; iterateValue++) {
            if (iterateValue == stopAt) {
                break;
            }
            completedLoops++;
        }
        return completedLoops;
    }

    /**
     * Continue statement example. The requested index is skipped.
     * @param noLoops - maximum number of loop iterations
     * @param skipAt - iteration to skip
     * @return number of completed iterations that were not skipped
     */
    public int continueLoop(int noLoops, int skipAt) {
        int completedLoops = 0;
        for (int iterateValue = 0; iterateValue < noLoops; iterateValue++) {
            if (iterateValue == skipAt) {
                continue;
            }
            completedLoops++;
        }
        return completedLoops;
    }

    /**
     * Try, catch, and finally statement example.
     * @param numerator - value to divide
     * @param denominator - value to divide by
     * @return division result, or -1 when division by zero is caught
     */
    public int tryCatchFinally(int numerator, int denominator) {
        try {
            return numerator / denominator;
        } catch (ArithmeticException exception) {
            return -1;
        } finally {
            // Finally runs whether the try block succeeds or catches an exception.
        }
    }

    /**
     * Explicit exception example.
     * @param shouldThrow - whether an exception should be thrown
     */
    public void throwException(boolean shouldThrow) {
        if (shouldThrow) {
            throw new IllegalArgumentException("Exception requested");
        }
    }

    /**
     * Recursive method example that calculates a factorial.
     * @param number - non-negative number to calculate
     * @return factorial of number
     */
    public long recursiveFactorial(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        if (number <= 1) {
            return 1;
        }
        return number * recursiveFactorial(number - 1);
    }

    /**
     * Method overloading example using a numeric value.
     * @param value - value to describe
     * @return value description
     */
    public String overloadedMethod(int value) {
        return "int:" + value;
    }

    /**
     * Method overloading example using text.
     * @param value - value to describe
     * @return value description
     */
    public String overloadedMethod(String value) {
        return "String:" + value;
    }

    /**
     * Control statement for while setup example. Initialization is required. Test is a start of loop.
     * @param noLoops number of loops being done
     * @return
     */
    public int[] whileLoop(int noLoops) {
        int[] retArray = new int[noLoops];
        int iterateValue = 0;
        while (iterateValue != noLoops) {
            retArray[iterateValue] = iterateValue * -1;
            iterateValue++;
        }
        return retArray;
    }

    /**
     * Control statement for repeat setup example. Test is at end of loop.
     * @param noLoops - number of repeats being done
     * @return
     */
    public int[] repeatLoop(int noLoops) {
        int[] retArray = new int[noLoops];
        int iterateValue = 0;
        do {
            retArray[iterateValue] = iterateValue * -1;
            iterateValue++;
        } while (iterateValue != noLoops);
        return retArray;
    }

    /**
     * Control statement for loop setup example.
     * @param noLoops - number of for loop repeat
     * @return
     */
    public int[] forLoop(int noLoops) {
        int[] retArray = new int[noLoops];
        for (int iterateValue = 0; iterateValue < noLoops; iterateValue++) {
            retArray[iterateValue] = iterateValue * -1;

        }
        return retArray;
    }

    /**
     * Control statement for for each loop.
     * @param fArray - array presenting what is collection that for executes on.
     * @return
     */
    public int[] forEachLoop(int[] fArray) {
        int[] retArray = new int[fArray.length];
        int idx = 0;
        for (int value : fArray) {
            retArray[idx] = value * -1;
            idx++;
        }
        return retArray;
    }

}
