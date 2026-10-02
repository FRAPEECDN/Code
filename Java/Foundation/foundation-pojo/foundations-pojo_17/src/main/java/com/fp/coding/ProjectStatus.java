package com.fp.coding;

/** Project lifecycle states and the allowed transition graph. */
public enum ProjectStatus {
    REGISTERED,
    PLANNED,
    IMPLEMENTATING,
    FINISHED,
    CANCEL;

    /** Returns whether this state may transition directly to the supplied state. */
    public boolean canTransitionTo(ProjectStatus nextStatus) {
        return switch (this) {
            case REGISTERED -> nextStatus == PLANNED || nextStatus == CANCEL;
            case PLANNED -> nextStatus == IMPLEMENTATING || nextStatus == CANCEL;
            case IMPLEMENTATING -> nextStatus == FINISHED || nextStatus == CANCEL;
            case FINISHED, CANCEL -> false;
        };
    }
}