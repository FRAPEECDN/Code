package foundation.io.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SerializationExamplesTest {
    @Test
    void roundTripsTheMessageThroughObjectStreamsAndJson() throws Exception {
        SerializationExamples.Report report = SerializationExamples.run();

        assertEquals(report.original(), report.objectRoundTrip());
        assertEquals(report.original(), report.jsonRoundTrip());
        assertEquals("Network foundations", report.jsonRoundTrip().getProjectName());
    }
}
