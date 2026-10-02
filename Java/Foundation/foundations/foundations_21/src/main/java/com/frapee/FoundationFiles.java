package com.frapee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Foundation guide to Path, Files, text, binary data, and resource cleanup.
 * The resource methods make try-with-resources visible; the convenience methods
 * show the shorter Files API for the same common operations.
 */
public class FoundationFiles {

    /**
     * Write text using the convenience Files API.
     * @param path destination path
     * @param value text to write
     * @throws IOException when the file cannot be written
     */
    public void writeText(Path path, String value) throws IOException {
        Files.writeString(path, value);
    }

    /**
     * Read text using the convenience Files API.
     * @param path source path
     * @return file contents as text
     * @throws IOException when the file cannot be read
     */
    public String readText(Path path) throws IOException {
        return Files.readString(path);
    }

    /**
     * Write UTF-8 text with an explicitly managed writer.
     * @param path destination path
     * @param value UTF-8 text to write
     * @throws IOException when writing fails
     */
    public void writeTextWithResource(Path path, String value) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(value);
        }
    }

    /**
     * Read UTF-8 text with an explicitly managed reader.
     * @param path source path
     * @return all lines joined with the platform line separator
     * @throws IOException when reading fails
     */
    public String readTextWithResource(Path path) throws IOException {
        StringBuilder value = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (value.length() > 0) {
                    value.append(System.lineSeparator());
                }
                value.append(line);
            }
        }
        return value.toString();
    }

    /**
     * Write raw bytes with an explicitly managed output stream.
     * @param path destination path
     * @param value raw bytes to write
     * @throws IOException when writing fails
     */
    public void writeBinaryWithResource(Path path, byte[] value) throws IOException {
        try (OutputStream output = Files.newOutputStream(path)) {
            output.write(value);
        }
    }

    /**
     * Read raw bytes with an explicitly managed input stream.
     * @param path source path
     * @return all bytes read from the file
     * @throws IOException when reading fails
     */
    public byte[] readBinaryWithResource(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            return input.readAllBytes();
        }
    }

    /** @param path path to inspect
     * @return true when the path exists */
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    /** @param directory base directory
     * @param fileName child name
     * @return resolved child path */
    public Path resolve(Path directory, String fileName) {
        return directory.resolve(fileName);
    }

    /** @param path path to delete
     * @throws IOException when deletion fails */
    public void deleteIfExists(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
