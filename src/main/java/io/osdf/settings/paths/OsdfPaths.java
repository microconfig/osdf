package io.osdf.settings.paths;

import io.osdf.actions.chaos.state.ChaosPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.settings.paths.SettingsPaths.settingsPaths;
import static java.lang.System.getenv;
import static java.nio.file.Path.of;
import static org.apache.commons.io.FileUtils.getUserDirectory;

@RequiredArgsConstructor
public class OsdfPaths {
    private final Path root;

    public static OsdfPaths paths() {
        String dirFromEnv = getenv("OSDF_WORKDIR");
        if (dirFromEnv != null && !dirFromEnv.trim().isEmpty()) return new OsdfPaths(of(dirFromEnv));

        return new OsdfPaths(of(getUserDirectory() + "/.osdf"));
    }

    public Path root() {
        return root;
    }

    public Path bin() {
        return of(root() + "/bin");
    }

    public Path settingsRoot() {
        return of(root() + "/settings");
    }

    public Path tmp() {
        return of(root() + "/tmp");
    }

    public Path configsDownloadPath() {
        return of(root() + "/fetchedConfigs");
    }

    public Path configsPath() {
        return of(configsDownloadPath() + "/repo");
    }

    public Path componentsPath() {
        return of(root() + "/buildConfigs");
    }

    public Path projectVersionPath() {
        return of(configsPath() + "/components/system/versions/project-version.proc");
    }

    public Path configVersionPath() {
        return of(configsPath() + "/components/system/versions/config-version.proc");
    }

    public SettingsPaths settings() {
        return settingsPaths(settingsRoot());
    }

    public ChaosPaths chaos() {
        return new ChaosPaths(of(root() + "/chaos"));
    }
}
