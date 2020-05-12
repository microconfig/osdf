package io.microconfig.osdf.configs;

import io.microconfig.osdf.configfetcher.ConfigsFetcher;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.settings.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.microconfig.osdf.configfetcher.ConfigsFetcher.fetcher;
import static io.microconfig.osdf.resources.ResourcesHashComputer.resourcesHashComputer;
import static io.microconfig.osdf.microconfig.MicroConfig.microConfig;
import static io.microconfig.osdf.microconfig.properties.PropertySetter.propertySetter;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.utils.Logger.warn;
import static java.nio.file.Files.list;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class ConfigsUpdater {
    private final OSDFPaths paths;
    private final SettingsFile<ConfigsSettings> settingsFile;

    public static ConfigsUpdater configsUpdater(OSDFPaths paths) {
        SettingsFile<ConfigsSettings> settingsFile = settingsFile(ConfigsSettings.class, paths.settings().configs());
        return new ConfigsUpdater(paths, settingsFile);
    }

    public void setConfigsSource(ConfigsSource configsSource) {
        settingsFile.setIfNotNull(ConfigsSettings::setConfigsSource, configsSource);
        settingsFile.save();
        fetch();
    }

    public void setConfigsParameters(String env, String projectVersion) {
        settingsFile.setIfNotNull(ConfigsSettings::setEnv, env);
        settingsFile.setIfNotNull(ConfigsSettings::setProjectVersion, projectVersion);
        settingsFile.save();
        buildConfigs();
    }

    public void fetch() {
        ConfigsSettings settings = settingsFile.getSettings();
        if (settings.getConfigsSource() == null) throw new OSDFException("Config source is not specified");

        ConfigsFetcher fetcher = fetcher(settings.getConfigsSource(), paths);
        fetcher.fetchConfigs();
        propertySetter().setIfNecessary(paths.configVersionPath(), "config.version", fetcher.getConfigVersion());
        if (settings.getEnv() == null) {
            warn("Environment is not specified");
        } else {
            buildConfigs();
        }
    }

    private void buildConfigs() {
        ConfigsSettings settings = settingsFile.getSettings();
        if (settings.getEnv() == null) throw new OSDFException("Environment is not specified");

        propertySetter().setIfNecessary(paths.projectVersionPath(), "project.version", settings.getProjectVersion());
        microConfig(settings.getEnv(), paths).generateConfigs(emptyList());
        computeHashes();
    }

    private void computeHashes() {
        try (Stream<Path> configDirs = list(paths.componentsPath())) {
            configDirs.forEach(configDir -> resourcesHashComputer(configDir).computeAll());
        } catch (IOException e) {
            throw new OSDFException("Can't access " + paths.componentsPath());
        }
    }
}
