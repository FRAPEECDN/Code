package foundation.io.java25;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StructuredConcurrencyExampleTest {
    @Test
    void joinsFileAndSerializationTasks() throws Exception {
        StructuredConcurrencyExample.Report report = StructuredConcurrencyExample.run();

        assertEquals("modern Path and Files", report.fileText());
        assertTrue(report.json().contains("Network foundations"));
    }
}
