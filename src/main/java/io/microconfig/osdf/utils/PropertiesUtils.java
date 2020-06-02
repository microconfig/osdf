package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import static java.nio.file.Files.newInputStream;

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
}
