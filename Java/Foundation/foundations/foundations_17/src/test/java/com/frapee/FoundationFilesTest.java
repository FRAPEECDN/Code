package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FoundationFilesTest {

    private final FoundationFiles files = new FoundationFiles();

    @Test
    public void testPathAndFileOperations() throws IOException {
        Path directory = Files.createTempDirectory("foundations");
        Path file = files.resolve(directory, "example.txt");
        try {
            assertThat(files.exists(file), equalTo(false));
            files.writeText(file, "file content");
            assertThat(files.exists(file), equalTo(true));
            assertThat(files.readText(file), equalTo("file content"));
        } finally {
            files.deleteIfExists(file);
            files.deleteIfExists(directory);
        }
    }

    @Test
    public void testTextTryWithResources() throws IOException {
        Path directory = Files.createTempDirectory("foundations-text");
        Path file = files.resolve(directory, "resource.txt");
        String content = "first line" + System.lineSeparator() + "second line";
        try {
            files.writeTextWithResource(file, content);
            assertThat(files.readTextWithResource(file), equalTo(content));
        } finally {
            files.deleteIfExists(file);
            files.deleteIfExists(directory);
        }
    }

    @Test
    public void testBinaryTryWithResources() throws IOException {
        Path directory = Files.createTempDirectory("foundations-binary");
        Path file = files.resolve(directory, "resource.bin");
        byte[] content = {0, 1, 2, 127, -1};
        try {
            files.writeBinaryWithResource(file, content);
            assertThat(Arrays.equals(files.readBinaryWithResource(file), content), equalTo(true));
        } finally {
            files.deleteIfExists(file);
            files.deleteIfExists(directory);
        }
    }
}