package io.osdf.actions.chaos.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.osdf.common.exceptions.PossibleBugException;

import java.io.IOException;
import java.nio.file.Path;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

public class MapperUtils {
    public static <T> T createFromPath(Path path, Class<T> clazz) {
        try {
            return new ObjectMapper()
                    .setVisibility(FIELD, ANY)
                    .readValue(path.toFile(), clazz);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't create " + clazz.getSimpleName() + " from " + path, e);
        }
    }

    public static void dump(Object object, Path path) {
        try {
            new ObjectMapper()
                    .setVisibility(FIELD, ANY)
                    .writeValue(path.toFile(), object);
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't dump object at path " + path, e);
        }
    }
}
