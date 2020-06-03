package io.microconfig.osdf.paths;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.paths.SettingsPaths.settingsPaths;
import static java.nio.file.Path.of;
import static org.apache.commons.io.FileUtils.getUserDirectory;

@RequiredArgsConstructor
public class OSDFPaths {
    private final Path root;

    public static OSDFPaths paths() {
        return new OSDFPaths(of(getUserDirectory() + "/.osdf"));
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

    public Path chaosPlanPath() {
        return of(componentsPath() + "/chaos/application.yaml");
    }

    public SettingsPaths settings() {
        return settingsPaths(settingsRoot());
    }
}
