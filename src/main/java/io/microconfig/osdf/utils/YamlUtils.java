package io.microconfig.osdf.utils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static java.nio.file.Files.newInputStream;

public class YamlUtils {
    public static <T> T createFromFile(Class<T> clazz, Path path) {
        try (InputStream inputStream = newInputStream(path)) {
            Yaml yaml = new Yaml(new Constructor(clazz));
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open file " + path);
        }
    }
}
