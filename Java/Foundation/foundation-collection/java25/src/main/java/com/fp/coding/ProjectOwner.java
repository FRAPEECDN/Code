package com.fp.coding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Owns projects registered to this owner and returns immutable list snapshots.
 * Synchronized methods serialize access; use ConcurrentHashMap.newKeySet() for a concurrent registry
 * when set semantics are appropriate instead of insertion-ordered list semantics.
 */
public final class ProjectOwner {
    private final List<Project> projects = new ArrayList<>();

    /** Creates an owner with an empty project registry. */
    public ProjectOwner() {}

    /**
     * Registers a project with this owner.
     *
     * @param project project to register
     * @throws NullPointerException if {@code project} is {@code null}
     */
    public synchronized void registerProject(Project project) {
        projects.add(Objects.requireNonNull(project, "project"));
    }

    /**
     * Returns an immutable snapshot of the registered projects.
     *
     * @return an immutable list snapshot
     */
    public synchronized List<Project> getProjects() {
        return Collections.unmodifiableList(new ArrayList<>(projects));
    }
}
