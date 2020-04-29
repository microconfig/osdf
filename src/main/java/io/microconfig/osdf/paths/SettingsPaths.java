package io.microconfig.osdf.paths;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class SettingsPaths {
    private final Path settingsRootPath;

    public static SettingsPaths settingsPaths(Path settingsRootPath) {
        return new SettingsPaths(settingsRootPath);
    }

    public Path gitFetcher() {
        return of(settingsRootPath + "/git.yaml");
    }

    public Path nexusFetcher() {
        return of(settingsRootPath + "/nexus.yaml");
    }

    public Path localFetcher() {
        return of(settingsRootPath + "/local.yaml");
    }

    public Path openshift() {
        return of(settingsRootPath + "/openshift.yaml");
    }

    public Path configs() {
        return of(settingsRootPath + "/configs.yaml");
    }
}
