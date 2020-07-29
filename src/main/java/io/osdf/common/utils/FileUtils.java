package io.osdf.common.utils;

import io.osdf.common.exceptions.PossibleBugException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Comparator.reverseOrder;

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
        } catch (DirectoryNotEmptyException e) {
            deleteDirectory(path);
        } catch (IOException e) {
            //file probably doesn't exist
        }
    }

    private static void deleteDirectory(Path dir) {
        try (Stream<Path> paths = walk(dir)) {
            paths.sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new PossibleBugException("Can't delete directory " + dir, e);
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
            throw new PossibleBugException("Can't create temporary folder", e);
        }
    }

    public static void move(Path source, Path target) {
        try {
            Files.move(source, target, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PossibleBugException("Can't move " + target, e);
        }
    }

    public static void copy(Path source, Path target) {
        try {
            Files.copy(source, target, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PossibleBugException("Can't move " + target, e);
        }
    }
}
