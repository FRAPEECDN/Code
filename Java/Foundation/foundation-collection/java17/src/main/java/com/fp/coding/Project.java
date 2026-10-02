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
 * Project details and a date-ordered lifecycle history owned by a project owner.
 * Status participates in Lombok-generated equality and hashing; avoid changing it while this object is a hash key.
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
     * Returns the owner that registered this project.
     * @return the registering owner
     */
    public ProjectOwner getProjectOwner() {
        return projectOwner;
    }

    /**
     * Returns the normalized project name.
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns the normalized sponsor name.
     * @return the sponsor name
     */
    public String getSponsor() {
        return sponsor;
    }

    /**
     * Returns the project budget.
     * @return the budget
     */
    public double getBudget() {
        return budget;
    }

    /**
     * Returns the planned start date.
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the project deadline.
     * @return the deadline
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the current lifecycle status.
     * @return the current status
     */
    public synchronized ProjectStatus getStatus() {
        return status;
    }

    /**
     * Creates and registers a project in the {@link ProjectStatus#REGISTERED} state.
     *
     * @param projectOwner owner that receives the new project
     * @param projectName nonblank project name
     * @param sponsor nonblank sponsor name
     * @param budget finite, nonnegative budget
     * @param startDate planned start date
     * @param deadline deadline on or after the start date
     * @param registeredOn date the project was registered
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
     * Returns an immutable snapshot of each lifecycle status and its transition date.
     *
     * @return an immutable status-to-date snapshot
     */
    public synchronized Map<ProjectStatus, LocalDate> getStatusHistory() {
        return Collections.unmodifiableMap(new EnumMap<>(statusHistory));
    }

    /**
     * Changes status only along an allowed transition and nondecreasing timeline.
     *
     * @param nextStatus allowed next lifecycle status
     * @param reachedOn date the new status was reached
     * @throws IllegalStateException if the transition is not allowed
     * @throws IllegalArgumentException if the date precedes the current status date
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
