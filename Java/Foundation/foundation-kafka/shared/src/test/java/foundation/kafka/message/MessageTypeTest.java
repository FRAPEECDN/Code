package foundation.kafka.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;

class MessageTypeTest {
    @Test
    void readsMessageTypeFromHeader() {
        RecordHeaders headers = new RecordHeaders();
        headers.add(MessageType.HEADER_NAME, MessageType.AVRO.headerValue());

        assertEquals(MessageType.AVRO, MessageType.fromHeaders(headers));
    }

    @Test
    void treatsUntaggedLegacyRecordsAsStrings() {
        assertEquals(MessageType.STRING, MessageType.fromHeaders(new RecordHeaders()));
    }

    @Test
    void rejectsUnknownMessageTypeHeaders() {
        RecordHeaders headers = new RecordHeaders();
        headers.add(MessageType.HEADER_NAME, "XML".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> MessageType.fromHeaders(headers));
    }
}
