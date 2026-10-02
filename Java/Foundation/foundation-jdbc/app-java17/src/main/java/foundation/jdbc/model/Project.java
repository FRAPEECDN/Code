package foundation.jdbc.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project details and a date-ordered lifecycle history owned by a project owner.
 *
 * <p>Lombok generates the builder, getters for immutable details, and value methods. Status changes
 * must use {@link #transitionTo(ProjectStatus, LocalDate)} so the transition graph and timeline stay valid.
 */
@Data
@ToString
@EqualsAndHashCode
public final class Project {
    private final ProjectOwner projectOwner;
    private final String projectName;
    private final String sponsor;
    private final double budget;
    private final LocalDate startDate;
    private final LocalDate deadline;
    @Setter(AccessLevel.NONE)
    private ProjectStatus status;
    @Getter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final Map<ProjectStatus, LocalDate> statusHistory;

    /**
     * Creates a registered project and registers it with its owner.
     *
     * @param projectOwner owner that receives this project
     * @param projectName nonblank project name; surrounding whitespace is removed
     * @param sponsor nonblank sponsor; surrounding whitespace is removed
     * @param budget finite, nonnegative budget
     * @param startDate planned start date
     * @param deadline deadline on or after the start date
     * @param registeredOn date the project entered {@link ProjectStatus#REGISTERED}
     * @throws IllegalArgumentException if text, budget, or date ordering is invalid
     * @throws NullPointerException if a required value is {@code null}
     */
    @Builder
    public Project(
            ProjectOwner projectOwner,
            String projectName,
            String sponsor,
            double budget,
            LocalDate startDate,
            LocalDate deadline,
            LocalDate registeredOn) {
        this.projectOwner = Objects.requireNonNull(projectOwner, "projectOwner");
        this.projectName = requireText(projectName, "projectName");
        this.sponsor = requireText(sponsor, "sponsor");
        if (!Double.isFinite(budget) || budget < 0) {
            throw new IllegalArgumentException("budget must be finite and 0 or above");
        }
        this.budget = budget;
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.deadline = Objects.requireNonNull(deadline, "deadline");
        if (deadline.isBefore(startDate)) {
            throw new IllegalArgumentException("deadline must not be before startDate");
        }
        LocalDate registrationDate = Objects.requireNonNull(registeredOn, "registeredOn");
        this.status = ProjectStatus.REGISTERED;
        this.statusHistory = new EnumMap<>(ProjectStatus.class);
        this.statusHistory.put(ProjectStatus.REGISTERED, registrationDate);
        projectOwner.registerProject(this);
    }

    /**
     * Returns the current lifecycle state.
     *
     * @return current project status
     */
    public synchronized ProjectStatus getStatus() {
        return status;
    }

    /**
     * Returns an immutable snapshot of the lifecycle state dates.
     *
     * @return immutable status-to-date snapshot
     */
    public synchronized Map<ProjectStatus, LocalDate> getStatusHistory() {
        return Collections.unmodifiableMap(new EnumMap<>(statusHistory));
    }

    /**
     * Applies one allowed transition on a nondecreasing status timeline.
     *
     * @param nextStatus allowed next state
     * @param reachedOn date the new state was reached
     * @throws IllegalStateException if the transition is not allowed
     * @throws IllegalArgumentException if the date precedes the current state date
     * @throws NullPointerException if either argument is {@code null}
     */
    public synchronized void transitionTo(ProjectStatus nextStatus, LocalDate reachedOn) {
        Objects.requireNonNull(nextStatus, "nextStatus");
        LocalDate transitionDate = Objects.requireNonNull(reachedOn, "reachedOn");
        if (!status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException("cannot transition project from " + status + " to " + nextStatus);
        }
        LocalDate previousDate = statusHistory.get(status);
        if (transitionDate.isBefore(previousDate)) {
            throw new IllegalArgumentException("status date must not precede the previous status date");
        }
        status = nextStatus;
        statusHistory.put(nextStatus, transitionDate);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.strip();
    }
}