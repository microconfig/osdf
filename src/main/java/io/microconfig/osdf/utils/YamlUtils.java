package io.microconfig.osdf.utils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

    public static String getOrNull(Map<String, Object> yaml, String... properties) {
        try {
            return getString(yaml, properties);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getString(Map<String, Object> yaml, String... properties) {
        return (String) getObjectOrNull(yaml, properties);
    }

    public static int getInt(Map<String, Object> yaml, String... properties) {
        return (int) getObjectOrNull(yaml, properties);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getList(Map<String, Object> yaml, String... properties) {
        return (List<Object>) getObjectOrNull(yaml, properties);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(Map<String, Object> yaml, String... properties) {
        return (Map<String, Object>) getObjectOrNull(yaml, properties);
    }

    public static Object getObjectOrNull(Map<String, Object> yaml, String... properties) {
        try {
            return getObject(yaml, properties);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getObject(Map<String, Object> yaml, String... properties) {
        Map<String, Object> current = yaml;
        for (int i = 0; i < properties.length - 1; i++) {
            current = get(current, properties[i]);
        }
        return current.get(properties[properties.length - 1]);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> get(Map<String, Object> properties, String property) {
        return (Map<String, Object>) properties.get(property);
    }
}
