package io.microconfig.osdf.settings;

import io.microconfig.osdf.exceptions.OSDFException;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.YamlUtils.createFromFile;

public class SettingsLoader {
    public static SettingsLoader settingsLoader() {
        return new SettingsLoader();
    }

    public <T extends VerifiableFile> T load(Class<T> clazz, Path path) {
        T settings = createFromFile(clazz, path);
        if (!settings.verify()) throw new OSDFException("Incomplete " + clazz.getSimpleName() + " configuration");
        return settings;
    }
}
