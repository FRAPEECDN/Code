package foundation.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Compares Java's built-in object serialization with a Jackson JSON
 * representation of the same value.
 *
 * <p>
 * Java object serialization writes a Java-specific binary stream containing
 * class and object-graph information.
 * Reading that stream reconstructs an object of a compatible Java class; it is
 * not a language-neutral contract.
 * This example installs an {@link ObjectInputFilter} before reading and permits
 * only the message class and the
 * required {@code java.base} classes. A filter limits which classes may be
 * reconstructed, but applications must
 * still avoid native deserialization for untrusted input.
 *
 * <p>
 * Jackson JSON writes named properties such as {@code projectName} and
 * {@code priority}. Its creator annotations
 * map those JSON names back to constructor arguments. The result is inspectable
 * text and can be consumed by other
 * languages, but compatibility still depends on an intentionally managed
 * schema. These formats are not
 * interchangeable: object-stream bytes cannot be parsed as JSON, and JSON does
 * not preserve Java object-graph
 * identity or class metadata.
 */
public final class SerializationExamples {
    private static final ObjectMapper JSON = new ObjectMapper();

    private SerializationExamples() {
    }

    /**
     * Serializes and restores a message using both Java object streams and Jackson
     * JSON.
     *
     * <ol>
     * <li>The object-stream path writes the message into a byte array, installs a
     * class filter on a new input stream,
     * and reconstructs the object with {@code readObject()}.</li>
     * <li>The JSON path asks Jackson to write named properties to a string, then
     * reads that string into a new
     * message using the {@code @JsonCreator} constructor.</li>
     * </ol>
     *
     * <p>
     * The test compares the reconstructed values, not their encoded bytes: the
     * binary stream and JSON have
     * different formats, metadata, and compatibility characteristics.
     *
     * @return the source message, the object-stream byte count, both encoded forms,
     *         and both restored values
     * @throws IOException            if Java object-stream serialization fails
     * @throws ClassNotFoundException if the serialized class cannot be resolved
     */
    public static Report run() throws IOException, ClassNotFoundException {
        ProjectMessage original = new ProjectMessage("Network foundations", 2);

        // ObjectOutputStream writes a stream header and Java class/object data into the
        // byte array.
        byte[] objectBytes;
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream objects = new ObjectOutputStream(bytes)) {
            objects.writeObject(original);
            objects.flush();
            objectBytes = bytes.toByteArray();
        }

        // Install the filter before readObject: ProjectMessage and required java.base
        // types are allowed;
        // every other class is rejected by the final !* pattern.
        ProjectMessage objectRoundTrip;
        try (ObjectInputStream objects = new ObjectInputStream(new ByteArrayInputStream(objectBytes))) {
            objects.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "foundation.io.serialization.ProjectMessage;java.base/*;!*"));
            objectRoundTrip = (ProjectMessage) objects.readObject();
        }

        // Jackson maps getProjectName/getPriority to JSON property names, then uses
        // @JsonCreator and
        // @JsonProperty to map those names back to constructor arguments.
        String json = JSON.writeValueAsString(original);
        ProjectMessage jsonRoundTrip = JSON.readValue(json, ProjectMessage.class);
        return new Report(original, objectBytes.length, objectRoundTrip, json, jsonRoundTrip);
    }

    /**
     * Results from native serialization and JSON round trips.
     *
     * @param original          source message
     * @param objectStreamBytes size of the Java object-stream representation
     * @param objectRoundTrip   value restored from the object stream
     * @param json              JSON representation of the source message
     * @param jsonRoundTrip     value restored from JSON
     */
    public record Report(
            ProjectMessage original,
            int objectStreamBytes,
            ProjectMessage objectRoundTrip,
            String json,
            ProjectMessage jsonRoundTrip) {
    }
}
