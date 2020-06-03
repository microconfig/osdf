package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.newInputStream;
import static org.yaml.snakeyaml.nodes.Tag.MAP;

public class YamlUtils {
    public static <T> T createFromFile(Class<T> clazz, Path path) {
        try (InputStream inputStream = newInputStream(path)) {
            Representer representer = new Representer();
            representer.getPropertyUtils().setSkipMissingProperties(true);
            Yaml yaml = new Yaml(new Constructor(clazz), representer);
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't open file " + path, e);
        }
    }

    public static <T> T createFromString(Class<T> clazz, String content) {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(clazz), representer);
        return yaml.load(content);
    }

    public static Map<String, Object> loadFromPath(Path path) {
        try {
            return new Yaml().load(new FileInputStream(path.toString()));
        } catch (FileNotFoundException e) {
            throw new PossibleBugException("Couldn't load yaml from path " + path, e);
        }
    }

    public static void dump(Object object, Path path) {
        try {
            Representer representer = new Representer();
            representer.addClassTag(object.getClass(), MAP);
            new Yaml(representer).dump(object, newBufferedWriter(path));
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't dump yaml file", e);
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
        return valueOf(getObjectOrNull(yaml, properties));
    }

    public static Integer getInt(Map<String, Object> yaml, String... properties) {
        return (Integer) getObjectOrNull(yaml, properties);
    }

    public static float getFloat(Map<String, Object> yaml, String... properties) {
        return (float) getObjectOrNull(yaml, properties);
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
