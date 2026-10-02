package com.fp.coding;

/** Project lifecycle states and the allowed transition graph. */
public enum ProjectStatus {
    /** Project has been registered with an owner. */
    REGISTERED,
    /** Project planning has been approved. */
    PLANNED,
    /** Project implementation is in progress. */
    IMPLEMENTATING,
    /** Project work is complete. */
    FINISHED,
    /** Project has been cancelled. */
    CANCEL;

    /**
     * Returns whether this state may transition directly to the supplied state.
     *
     * @param nextStatus candidate next state
     * @return {@code true} if the transition is allowed
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
