package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class FileUtils {
    public static String readAll(Path file) {
        try {
            return readString(file);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't read file " + file, e);
        }
    }

    public static String readAllFromResource(String resource) {
        InputStream input = FileUtils.class.getClassLoader().getResourceAsStream(resource);
        if (input == null) throw new PossibleBugException("Couldn't open resource " + resource);
        try {
            return IOUtils.toString(input, UTF_8.name());
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't read resource " + resource, e);
        }
    }

    public static void writeStringToFile(Path file, String content) {
        try {
            writeString(file, content);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't create file " + file, e);
        }
    }

    public static void appendToFile(Path file, String content) {
        try {
            writeString(file, content, APPEND, CREATE);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't create file " + file, e);
        }
    }

    public static void copyFile(Path from, Path to) {
        try {
            org.apache.commons.io.FileUtils.copyFile(from.toFile(), to.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't copy file " + from.getFileName() + ", to " + to.getFileName());
        }
    }

    public static void copyDirectory(Path from, Path to) {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(from.toFile(), to.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't copy directory " + from.getFileName() + ", to " + to.getFileName());
        }
    }

    public static void deleteDirectory(Path path) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't delete directory " + path.getFileName());
        }
    }

    public static void deleteIfEmpty(Path dir) {
        try (Stream<Path> list = list(dir)) {
            if (list.findAny().isEmpty()) {
                delete(dir);
            }
        } catch (IOException e) {
            throw new PossibleBugException("Can't delete folder " + dir, e);
        }
    }

    public static void createDirectoryIfNotExists(Path dir) {
        if (!exists(dir)) {
            try {
                createDirectory(dir);
            } catch (IOException e) {
                throw new PossibleBugException("Can't create directory " + dir, e);
            }
        }
    }

    public static String hashOfFile(Path path) {
        return md5Hex(readAll(path));
    }
}
