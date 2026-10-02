package com.frapee;

import java.util.List;

/**
 * Foundation example for the callback pattern using a custom functional interface.
 */
public class FoundationCallback {

    private final RangeCallback callback;

    /** @param callback callback implementation invoked by this example */
    public FoundationCallback(RangeCallback callback) {
        this.callback = callback;
    }

    /** @return values produced by the callback for the range 1 through 10 */
    public List<Integer> runCallback() {
        return runCallback(1, 10);
    }

    /** @param start inclusive range start
     * @param stop inclusive range end
     * @return values produced by the callback */
    public List<Integer> runCallback(int start, int stop) {
        return callback.generateList(start, stop);
    }
}
