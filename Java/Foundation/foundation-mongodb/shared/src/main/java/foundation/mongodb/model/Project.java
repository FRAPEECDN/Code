package foundation.mongodb.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project details and the date-ordered lifecycle history owned by a project
 * owner.
 *
 * <p>
 * A project receives a stable UUID when it is constructed. Its initial state is
 * {@link ProjectStatus#REGISTERED}; subsequent state changes must use
 * {@link #transitionTo(ProjectStatus, LocalDate)} so the transition graph and
 * history
 * remain consistent. Construction also registers the project with its owner.
 *
 * <p>
 * Lombok generates accessors for the project details and value methods. The
 * status
 * history is exposed only as an immutable snapshot.
 */
@Data
@ToString
@EqualsAndHashCode
public final class Project {
    private final UUID projectId;
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
     * Creates a registered project and adds it to its owner's registry.
     *
     * <p>
     * When {@code projectId} is {@code null}, a random UUID is generated. Project
     * names and sponsors are stripped of surrounding whitespace.
     *
     * @param projectId    stable project identity, or {@code null} to generate one
     * @param projectOwner owner that receives this project
     * @param projectName  nonblank project name
     * @param sponsor      nonblank project sponsor
     * @param budget       finite, nonnegative budget
     * @param startDate    planned start date
     * @param deadline     deadline on or after the start date
     * @param registeredOn date the project entered {@link ProjectStatus#REGISTERED}
     * @throws IllegalArgumentException if text, budget, or date ordering is invalid
     * @throws NullPointerException     if a required value other than
     *                                  {@code projectId} is null
     */
    @Builder
    public Project(
            UUID projectId,
            ProjectOwner projectOwner,
            String projectName,
            String sponsor,
            double budget,
            LocalDate startDate,
            LocalDate deadline,
            LocalDate registeredOn) {
        this.projectId = projectId == null ? UUID.randomUUID() : projectId;
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
     * @return the current state
     */
    public synchronized ProjectStatus getStatus() {
        return status;
    }

    /**
     * Returns an immutable snapshot of lifecycle states and the dates they were
     * reached.
     *
     * @return a detached, immutable status-to-date map
     */
    public synchronized Map<ProjectStatus, LocalDate> getStatusHistory() {
        return Collections.unmodifiableMap(new EnumMap<>(statusHistory));
    }

    /**
     * Advances the project through one permitted lifecycle transition.
     *
     * @param nextStatus target lifecycle state
     * @param reachedOn  date the target state was reached, not before the previous
     *                   state date
     * @throws IllegalStateException    if the current state cannot transition to
     *                                  {@code nextStatus}
     * @throws IllegalArgumentException if {@code reachedOn} precedes the previous
     *                                  state date
     * @throws NullPointerException     if either argument is null
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
