package foundation.mongodb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Owns projects registered to an identity that remains stable across
 * persistence reads.
 *
 * <p>
 * Project lists are maintained in memory and returned as immutable snapshots.
 * Owner
 * equality and hashing are based only on the persistent UUID, not on the
 * mutable project list.
 */
public final class ProjectOwner {
    private final UUID id;
    private final List<Project> projects = new ArrayList<>();

    /** Creates an owner with a new persistent identity and an empty registry. */
    public ProjectOwner() {
        this(UUID.randomUUID());
    }

    /**
     * Reconstitutes an owner with its persistent identity.
     *
     * @param id persistent owner UUID
     * @throws NullPointerException if {@code id} is null
     */
    public ProjectOwner(UUID id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    /**
     * Returns this owner's stable persistence identity.
     *
     * @return owner UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Adds a project to this owner's in-memory registry.
     *
     * @param project project to register
     * @throws NullPointerException if {@code project} is null
     */
    public synchronized void registerProject(Project project) {
        projects.add(Objects.requireNonNull(project, "project"));
    }

    /**
     * Returns an immutable snapshot of the registered projects.
     *
     * @return detached, immutable project list
     */
    public synchronized List<Project> getProjects() {
        return Collections.unmodifiableList(new ArrayList<>(projects));
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ProjectOwner owner && id.equals(owner.id);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
