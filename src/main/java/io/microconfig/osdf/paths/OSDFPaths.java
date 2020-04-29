package io.microconfig.osdf.paths;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

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

    public Path scriptFolder() {
        return of(root() + "/bin");
    }

    public Path configsDownloadPath() {
        return of(root() + "/fetchedConfigs");
    }

    public Path configPath() {
        return of(configsDownloadPath() + "/repo");
    }

    public Path componentsPath() {
        return of(root() + "/buildConfigs");
    }

    public Path projectVersionPath() {
        return of( configPath() + "/components/system/versions/project-version.proc");
    }

    public Path configVersionPath() {
        return of(configPath() + "/components/system/versions/config-version.proc");
    }

    public Path stateSavePath() {
        return of(root() + "/conf.yml");
    }

    public Path newStateSavePath() {
        return of(root() + "/conf_new.yml");
    }

    public Path oldStateSavePath() {
        return of(root() + "/conf_old.yml");
    }

    public Path settingsRootPath() {
        return of(root() + "/settings");
    }
}
