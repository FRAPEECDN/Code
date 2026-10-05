package com.fp.coding;

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
 * Project details and a date-ordered lifecycle history owned by a project
 * owner.
 *
 * <p>
 * Construction starts the project in {@link ProjectStatus#REGISTERED}, records
 * the registration date, and
 * registers the project with its owner. Later changes must use
 * {@link #transitionTo(ProjectStatus, LocalDate)} so
 * the allowed transition graph and nondecreasing history dates remain
 * consistent. Lombok generates value methods;
 * the mutable status history is excluded from equality and string output.
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

    /** @return the owner that registered this project */
    public ProjectOwner getProjectOwner() {
        return projectOwner;
    }

    /** @return the normalized project name */
    public String getProjectName() {
        return projectName;
    }

    /** @return the normalized sponsor name */
    public String getSponsor() {
        return sponsor;
    }

    /** @return finite, nonnegative project budget */
    public double getBudget() {
        return budget;
    }

    /** @return planned start date */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** @return deadline, which is on or after the start date */
    public LocalDate getDeadline() {
        return deadline;
    }

    /** @return current lifecycle state */
    public synchronized ProjectStatus getStatus() {
        return status;
    }

    /**
     * Creates a registered project and registers it with its owner.
     *
     * @param projectOwner owner that receives this project
     * @param projectName  nonblank name; surrounding whitespace is removed
     * @param sponsor      nonblank sponsor; surrounding whitespace is removed
     * @param budget       finite, nonnegative budget
     * @param startDate    planned start date
     * @param deadline     deadline on or after {@code startDate}
     * @param registeredOn date the project entered {@link ProjectStatus#REGISTERED}
     * @throws IllegalArgumentException if text, budget, or date ordering is invalid
     * @throws NullPointerException     if a required reference is null
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
     * Returns an immutable snapshot of lifecycle states and the dates they were
     * reached.
     *
     * @return detached, unmodifiable status-to-date map
     */
    public synchronized Map<ProjectStatus, LocalDate> getStatusHistory() {
        return Collections.unmodifiableMap(new EnumMap<>(statusHistory));
    }

    /**
     * Changes status only along an allowed transition and nondecreasing timeline.
     *
     * @param nextStatus target lifecycle state
     * @param reachedOn  date the target state was reached
     * @throws IllegalStateException    if the current state cannot transition to
     *                                  {@code nextStatus}
     * @throws IllegalArgumentException if {@code reachedOn} precedes the current
     *                                  status date
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