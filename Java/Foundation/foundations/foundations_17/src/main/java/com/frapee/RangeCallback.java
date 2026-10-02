package com.frapee;

import java.util.List;

/**
 * Functional callback contract for generating an integer range.
 */
@FunctionalInterface
public interface RangeCallback {

    /** @param start inclusive range start
     * @param stop inclusive range end
     * @return generated range values */
    List<Integer> generateList(int start, int stop);
}
