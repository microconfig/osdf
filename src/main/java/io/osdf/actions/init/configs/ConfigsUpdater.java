package io.osdf.actions.init.configs;

import io.osdf.actions.init.configs.fetch.ConfigsFetcher;
import io.osdf.actions.init.configs.postprocess.AppPostProcessor;
import io.osdf.actions.system.install.FileReplacer;
import io.osdf.common.SettingsFile;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static io.microconfig.utils.Logger.warn;
import static io.osdf.actions.init.configs.fetch.ConfigsFetcher.fetcher;
import static io.osdf.actions.init.configs.postprocess.AppPostProcessor.componentPostProcessor;
import static io.osdf.actions.system.install.AutoCompleteInstaller.autoCompleteInstaller;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.application.core.files.loaders.filters.AppFilter.isApp;
import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.osdf.core.local.microconfig.MicroConfig.microConfig;
import static io.osdf.core.local.microconfig.property.PropertySetter.propertySetter;
import static java.util.Collections.emptyList;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ConfigsUpdater {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final SettingsFile<ConfigsSettings> settingsFile;

    @Setter
    private boolean logoutOnBuild = true;

    public static ConfigsUpdater configsUpdater(OsdfPaths paths, ClusterCli cli) {
        SettingsFile<ConfigsSettings> settingsFile = settingsFile(ConfigsSettings.class, paths.settings().configs());
        return new ConfigsUpdater(paths, cli, settingsFile);
    }

    public void setConfigsSource(ConfigsSource configsSource) {
        settingsFile.setIfNotNull(ConfigsSettings::setConfigsSource, configsSource);
        settingsFile.save();
        fetch();
    }

    public ConfigsUpdater setConfigsParameters(String env, String projectVersion) {
        settingsFile.setIfNotNull(ConfigsSettings::setEnv, env);
        settingsFile.setIfNotNull(ConfigsSettings::setProjectVersion, projectVersion);
        settingsFile.save();
        return this;
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

    public void buildConfigs() {
        ConfigsSettings settings = settingsFile.getSettings();
        if (settings.getEnv() == null) throw new OSDFException("Environment is not specified");

        propertySetter().setIfNecessary(paths.projectVersionPath(), "project.version", settings.getProjectVersion());
        microConfig(settings.getEnv(), paths).generateConfigs(emptyList());

        AppPostProcessor appPostProcessor = componentPostProcessor();
        componentsLoader()
                .load(componentsFinder(paths.componentsPath()), isApp())
                .forEach(appPostProcessor::process);

        if (logoutOnBuild) cli.logout();
        updateAutocomplete();
    }

    private void updateAutocomplete() {
        FileReplacer autocompleteInstaller = autoCompleteInstaller(paths, true);
        autocompleteInstaller.prepare();
        autocompleteInstaller.replace();
    }
}
