package com.fp.coding;

/**
 * Project lifecycle states and the permitted direct transitions.
 *
 * <p>
 * {@link #FINISHED} and {@link #CANCEL} are terminal states. Cancellation is
 * allowed from any active state.
 */
public enum ProjectStatus {
    /** Project has been registered with its owner. */
    REGISTERED,
    /** Project planning has been approved. */
    PLANNED,
    /** Project implementation is in progress. */
    IMPLEMENTATING,
    /** Project work has completed; this state is terminal. */
    FINISHED,
    /** Project work has been cancelled; this state is terminal. */
    CANCEL;

    /**
     * Returns whether this state may transition directly to the supplied state.
     *
     * @param nextStatus candidate destination state
     * @return {@code true} when the direct transition is allowed
     */
    public boolean canTransitionTo(ProjectStatus nextStatus) {
        return switch (this) {
            case REGISTERED -> nextStatus == PLANNED || nextStatus == CANCEL;
            case PLANNED -> nextStatus == IMPLEMENTATING || nextStatus == CANCEL;
            case IMPLEMENTATING -> nextStatus == FINISHED || nextStatus == CANCEL;
            case FINISHED, CANCEL -> false;
        };
    }
}