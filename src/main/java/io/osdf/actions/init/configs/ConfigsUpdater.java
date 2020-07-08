package io.osdf.actions.init.configs;

import io.osdf.actions.init.configs.fetch.ConfigsFetcher;
import io.osdf.actions.init.configs.postprocess.ComponentPostProcessor;
import io.osdf.common.SettingsFile;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.warn;
import static io.osdf.actions.init.configs.fetch.ConfigsFetcher.fetcher;
import static io.osdf.actions.init.configs.postprocess.ComponentPostProcessor.componentPostProcessor;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.osdf.core.local.microconfig.MicroConfig.microConfig;
import static io.osdf.core.local.microconfig.property.PropertySetter.propertySetter;
import static java.nio.file.Files.exists;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class ConfigsUpdater {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final SettingsFile<ConfigsSettings> settingsFile;

    public static ConfigsUpdater configsUpdater(OsdfPaths paths, ClusterCli cli) {
        SettingsFile<ConfigsSettings> settingsFile = settingsFile(ConfigsSettings.class, paths.settings().configs());
        return new ConfigsUpdater(paths, cli, settingsFile);
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

        ComponentPostProcessor componentPostProcessor = componentPostProcessor();
        componentsLoader()
                .load(componentsFinder(
                        paths.componentsPath()),
                        component -> exists(component.getPath("resources")))
                .forEach(componentPostProcessor::process);
        cli.logout();
    }
}
