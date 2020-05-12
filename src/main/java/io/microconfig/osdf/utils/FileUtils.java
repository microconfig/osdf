package io.microconfig.osdf.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
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
            throw new RuntimeException("Couldn't read file " + file, e);
        }
    }

    public static String readAllFromResource(String resource) {
        InputStream input = FileUtils.class.getClassLoader().getResourceAsStream(resource);
        if (input == null) throw new RuntimeException("Couldn't open resource " + resource);
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
            throw new RuntimeException("Couldn't create file " + file, e);
        }
    }

    public static void appendToFile(Path file, String content) {
        try {
            writeString(file, content, APPEND, CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file " + file, e);
        }
    }

    public static void copyFile(Path from, Path to) {
        File fileFrom = new File(from.toString());
        File fileTo = new File(to.toString());

        try {
            org.apache.commons.io.FileUtils.copyFile(fileFrom, fileTo);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't copy file " + fileFrom.getName() + ", to " + fileTo.getName());
        }
    }

    public static void deleteIfEmpty(Path dir) {
        try (Stream<Path> list = list(dir)) {
            if (list.findAny().isEmpty()) {
                delete(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't delete folder " + dir);
        }
    }

    public static void createDirectoryIfNotExists(Path dir) {
        if (!exists(dir)) {
            try {
                createDirectory(dir);
            } catch (IOException e) {
                throw new RuntimeException("Can't create directory " + dir);
            }
        }
    }

    public static String hashOfFile(Path path) {
        return md5Hex(readAll(path));
    }

    public static void createFileIfNotExists(Path file) {
        if (!exists(file)) {
            try {
                createFile(file);
            } catch (IOException e) {
                throw new RuntimeException("Can't create file " + file);
            }
        }
    }

    public static Stream<Path> getPathsInDir(Path dir) {
        try {
            return list(dir);
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't open dir at " + dir, e);
        }
    }
}
