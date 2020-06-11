package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.lang.System.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.stream.Collectors.joining;
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
            throw new PossibleBugException("Couldn't read resource " + resource, e);
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
            throw new PossibleBugException("Couldn't copy file " + from + ", to " + to);
        }
    }

    public static void deleteDirectory(Path path) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't delete directory " + path.getFileName());
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

    public static void createFileIfNotExists(Path file) {
        if (!exists(file)) {
            try {
                createFile(file);
            } catch (IOException e) {
                throw new PossibleBugException("Can't create directory " + file, e);
            }
        }
    }

    public static String getContentFromResource(Path resourcePath) {
        try (InputStream inputStream = FileUtils.class.getResourceAsStream(resourcePath.toString())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines()
                    .collect(joining(lineSeparator()));
        } catch (IOException e) {
            throw new PossibleBugException("Can't get content from " + resourcePath, e);
        }
    }

    public static void createDirectoriesIfNotExists(Path dirs) {
        if (!exists(dirs)) {
            try {
                createDirectories(dirs);
            } catch (IOException e) {
                throw new PossibleBugException("Can't create directories " + dirs, e);
            }
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
