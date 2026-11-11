package foundation.kafka.message;

import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

public enum MessageType {
    STRING,
    JSON,
    AVRO;

    public static final String HEADER_NAME = "foundation-kafka-message-type";

    public byte[] headerValue() {
        return name().getBytes(StandardCharsets.UTF_8);
    }

    public static MessageType fromHeaders(Headers headers) {
        Header header = headers.lastHeader(HEADER_NAME);
        if (header == null) {
            return STRING;
        }
        if (header.value() == null) {
            throw new IllegalArgumentException("Kafka message type header must not be null");
        }

        String value = new String(header.value(), StandardCharsets.UTF_8);
        try {
            return valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported Kafka message type header: " + value, exception);
        }
    }
}
