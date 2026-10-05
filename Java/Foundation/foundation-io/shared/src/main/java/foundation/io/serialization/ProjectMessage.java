package foundation.io.serialization;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Small immutable message used to compare Java object serialization with JSON.
 *
 * <p>
 * The final fields make a deserialized message immutable after construction.
 * The JavaBean getters define the
 * JSON property names, while {@link JsonCreator} and {@link JsonProperty} make
 * Jackson's constructor mapping explicit.
 * Keeping an explicit serial version UID makes Java's class-version check
 * visible in the object-stream example.
 *
 * @see SerializationExamples
 */
public final class ProjectMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Project name carried by the message. */
    private final String projectName;
    /** Priority value carried by the message. */
    private final int priority;

    /**
     * Creates a message from the values carried by either serialized
     * representation.
     *
     * @param projectName project name carried by the message; must not be null
     * @param priority    priority value carried by the message
     * @throws NullPointerException if {@code projectName} is null
     */
    @JsonCreator
    public ProjectMessage(
            @JsonProperty("projectName") String projectName,
            @JsonProperty("priority") int priority) {
        this.projectName = Objects.requireNonNull(projectName, "projectName");
        this.priority = priority;
    }

    /**
     * Returns the project name carried by this message.
     *
     * @return project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns the message priority.
     *
     * @return priority value
     */
    public int getPriority() {
        return priority;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ProjectMessage message
                && priority == message.priority
                && projectName.equals(message.projectName);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(projectName, priority);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ProjectMessage[projectName=" + projectName + ", priority=" + priority + "]";
    }
}
