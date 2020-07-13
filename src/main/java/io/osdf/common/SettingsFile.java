package io.osdf.common;

import io.osdf.common.exceptions.PossibleBugException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import static io.osdf.common.utils.YamlUtils.createFromFile;
import static io.osdf.common.utils.YamlUtils.dump;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class SettingsFile<T> {
    @Getter
    private final T settings;
    private final Path path;

    public static <T> SettingsFile<T> settingsFile(Class<T> clazz, Path path) {
        T file = exists(path) ? createFromFile(clazz, path) : createEmpty(clazz);
        return new SettingsFile<>(file, path);
    }

    private static <T> T createEmpty(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PossibleBugException("Can't create " + clazz.getSimpleName(), e);
        }
    }

    public <V> void setIfNotNull(BiConsumer<T, ? super V> setter, V value) {
        if (value != null) setter.accept(settings, value);
    }

    public void save() {
        dump(settings, path);
    }
}
