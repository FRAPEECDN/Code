package foundation.mongodb.model;

/**
 * Project lifecycle states and the allowed direct-transition graph.
 *
 * <p>
 * Terminal states are {@link #FINISHED} and {@link #CANCEL}; neither can
 * transition
 * to another state.
 */
public enum ProjectStatus {
    /** Project has been registered with its owner. */
    REGISTERED,
    /** Planning has been approved. */
    PLANNED,
    /** Implementation work is in progress. */
    IMPLEMENTATING,
    /** Project work has completed. */
    FINISHED,
    /** Project work has been cancelled. */
    CANCEL;

    /**
     * Returns whether this state may transition directly to the supplied state.
     *
     * @param nextStatus candidate state
     * @return {@code true} if this direct transition is allowed
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
