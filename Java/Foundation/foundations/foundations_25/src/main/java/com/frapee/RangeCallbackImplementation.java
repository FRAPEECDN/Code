package com.frapee;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete range callback used to demonstrate a custom functional interface.
 */
public class RangeCallbackImplementation implements RangeCallback {

    /** @param start inclusive range start
     * @param stop inclusive range end
     * @return a list containing every value in the range */
    @Override
    public List<Integer> generateList(int start, int stop) {
        List<Integer> values = new ArrayList<>();
        for (int value = start; value <= stop; value++) {
            values.add(value);
        }
        return values;
    }
}
