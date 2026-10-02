package foundation.jdbc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Owns projects registered to this owner and returns immutable list snapshots.
 *
 * <p>The UUID is stable across database reads and is used as the relational owner key.
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
     * @return immutable project list snapshot
     */
    public synchronized List<Project> getProjects() {
        return Collections.unmodifiableList(new ArrayList<>(projects));
    }
}