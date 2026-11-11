package foundation.kafka.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DemoMessageCodecTest {
    private final DemoMessageCodec codec = new DemoMessageCodec();

    @Test
    void roundTripsPojoAsJson() throws Exception {
        DemoMessage original = new DemoMessage("json-1", "JSON payload", 123456789L);

        DemoMessage decoded = codec.fromJson(codec.toJson(original));

        assertMessageEquals(original, decoded);
    }

    @Test
    void roundTripsPojoAsAvroBinary() throws Exception {
        DemoMessage original = new DemoMessage("avro-1", "Avro payload", 987654321L);

        DemoMessage decoded = codec.fromAvro(codec.toAvro(original));

        assertMessageEquals(original, decoded);
    }

    private static void assertMessageEquals(DemoMessage expected, DemoMessage actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getCreatedAtEpochMillis(), actual.getCreatedAtEpochMillis());
    }
}
