package foundation.io.files;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FileIoExamplesTest {
    @Test
    void readsAndWritesThroughStreamsReadersNioAndAsyncChannel() throws Exception {
        FileIoExamples.Report report = FileIoExamples.run();

        assertEquals("classic byte stream", report.streamText());
        assertEquals("classic character reader | second line", report.readerText());
        assertEquals("modern Path and Files", report.nioText());
        assertEquals("asynchronous file channel", report.asyncText());
        assertEquals(4, report.directoryEntries().size());
    }
}
