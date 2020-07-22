package io.osdf.common.utils;

import io.osdf.common.exceptions.PossibleBugException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileUtils {
    public static String readAll(Path file) {
        try {
            return readString(file);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't read file " + file, e);
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

    public static void deleteIfEmpty(Path dir) {
        try (Stream<Path> list = list(dir)) {
            if (list.findAny().isEmpty()) {
                delete(dir);
            }
        } catch (IOException e) {
            throw new PossibleBugException("Can't delete folder " + dir, e);
        }
    }

    public static void delete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            //file probably doesn't exist
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

    public static Path createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        } catch (IOException e) {
            throw new PossibleBugException("Can't create temporary folder");
        }
    }
}
