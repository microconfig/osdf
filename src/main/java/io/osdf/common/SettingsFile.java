package io.osdf.common;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.exceptions.PossibleBugException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import static io.osdf.common.encryption.Encryption.encryptor;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.utils.YamlUtils.createFromString;
import static java.nio.file.Files.exists;

@RequiredArgsConstructor
public class SettingsFile<T> {
    @Getter
    private final T settings;
    private final Path path;

    public static <T> SettingsFile<T> settingsFile(Class<T> clazz, Path path) {
        try {
            T settings = exists(path) ? createFromString(clazz, decryptedContent(path)) : createEmpty(clazz);
            return new SettingsFile<>(settings, path);
        } catch (YAMLException e) {
            throw new OSDFException("Couldn't parse settings file at " + path + ". Check your encryption settings");
        }
    }

    private static String decryptedContent(Path path) {
        String content = readAll(path);
        try {
            return encryptor.decrypt(content);
        } catch (EncryptionOperationNotPossibleException e) {
            return content;
        }
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
        String content = new Yaml().dump(settings);
        String encryptedContent = encryptor.encrypt(content);
        writeStringToFile(path, encryptedContent);
    }
}
