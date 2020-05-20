package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Properties;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;

public class PropertiesUtils {
    public static Properties loadFromPath(Path path) {
        try (InputStream inputStream = newInputStream(path)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't open file at " + path, e);
        }
    }

    public static void dumpProperties(Properties props, Path path) {
        try {
            OutputStream outputStream = newOutputStream(path);
            props.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write in file at " + path, e);
        }
    }
}
