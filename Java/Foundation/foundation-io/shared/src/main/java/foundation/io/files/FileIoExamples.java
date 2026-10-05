package foundation.io.files;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Runnable comparisons of classic {@code java.io}, NIO.2
 * {@link Path}/{@link Files}, and asynchronous file channels.
 *
 * <p>
 * Byte streams move uninterpreted bytes. Readers and writers convert between
 * bytes and characters using a
 * charset, which is why this example always names UTF-8 rather than relying on
 * the machine's default charset.
 * NIO.2 adds path-based convenience methods and resource traversal.
 * {@link AsynchronousFileChannel} is a separate
 * facility that starts an operation and reports completion later; it is not
 * what makes ordinary {@code Files}
 * methods asynchronous.
 *
 * <p>
 * Each run creates a private temporary directory and deletes it before
 * returning, so the example does not
 * leave sample data in the working directory.
 */
public final class FileIoExamples {
    private FileIoExamples() {
    }

    /**
     * Writes and reads sample content through four I/O styles.
     *
     * <ol>
     * <li>A byte {@link OutputStream} writes UTF-8 bytes and an {@link InputStream}
     * reads them back; the bytes are
     * explicitly decoded only after the read.</li>
     * <li>A character {@link java.io.Writer} writes text and a
     * {@link BufferedReader} reads lines, hiding the
     * platform line separator from the returned value.</li>
     * <li>{@link Files#writeString(Path, CharSequence, java.nio.file.OpenOption...)}
     * and
     * {@link Files#readString(Path)} perform whole-file text operations using an
     * explicit charset.</li>
     * <li>An {@link AsynchronousFileChannel} starts a positional write and returns
     * a {@link java.util.concurrent.Future};
     * this demonstration calls {@code get()} to wait for completion before reading
     * the file.</li>
     * </ol>
     *
     * <p>
     * The last step intentionally waits, so the sample is deterministic. In an
     * application that has other work to
     * do, the caller could continue and observe the future later or use a
     * completion handler.
     *
     * @return values read back by each approach and the temporary directory's file
     *         names
     * @throws IOException          if a file operation fails
     * @throws ExecutionException   if the asynchronous write fails
     * @throws InterruptedException if the asynchronous write is interrupted
     */
    public static Report run() throws IOException, ExecutionException, InterruptedException {
        Path directory = Files.createTempDirectory("foundation-io-");
        Path streamFile = directory.resolve("stream.bin");
        Path readerFile = directory.resolve("reader.txt");
        Path nioFile = directory.resolve("nio.txt");
        Path asyncFile = directory.resolve("async.txt");

        try {
            // Streams handle bytes, not text. Encoding and decoding are separate, explicit
            // steps.
            byte[] binaryContent = "classic byte stream".getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = Files.newOutputStream(streamFile)) {
                output.write(binaryContent);
            }

            String streamText;
            try (InputStream input = Files.newInputStream(streamFile);
                    ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                input.transferTo(output);
                streamText = output.toString(StandardCharsets.UTF_8);
            }

            // Readers and writers handle characters. BufferedReader.lines() removes line
            // terminators;
            // joining with " | " makes that transformation visible in the report.
            try (var writer = Files.newBufferedWriter(readerFile, StandardCharsets.UTF_8)) {
                writer.write("classic character reader");
                writer.newLine();
                writer.write("second line");
            }
            String readerText;
            try (BufferedReader reader = Files.newBufferedReader(readerFile, StandardCharsets.UTF_8)) {
                readerText = reader.lines().collect(Collectors.joining(" | "));
            }

            // Path identifies a location; Files supplies common whole-file operations over
            // that path.
            Files.writeString(nioFile, "modern Path and Files", StandardCharsets.UTF_8);
            String nioText = Files.readString(nioFile, StandardCharsets.UTF_8);

            // The channel write is asynchronous at the API boundary. get() is the explicit
            // synchronization point.
            try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                    asyncFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
                ByteBuffer content = StandardCharsets.UTF_8.encode("asynchronous file channel");
                channel.write(content, 0).get();
            }
            String asyncText = Files.readString(asyncFile, StandardCharsets.UTF_8);
            List<String> entries;
            // Files.list returns a lazily consumed stream that owns an open directory
            // handle.
            try (var paths = Files.list(directory)) {
                entries = paths.map(path -> path.getFileName().toString()).sorted().toList();
            }

            return new Report(streamText, readerText, nioText, asyncText, entries);
        } finally {
            // Delete children before their parent directory; the finally block also runs
            // after a failed write/read.
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    /**
     * Values read back from the temporary files by each I/O example.
     *
     * @param streamText       text decoded from the byte-stream example
     * @param readerText       lines read by the character-reader example
     * @param nioText          text read through {@code Files.readString}
     * @param asyncText        text written through {@code AsynchronousFileChannel}
     * @param directoryEntries file names observed before cleanup
     */
    public record Report(
            String streamText,
            String readerText,
            String nioText,
            String asyncText,
            List<String> directoryEntries) {
    }
}
