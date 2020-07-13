package io.osdf.actions.configs;

import io.osdf.actions.configs.commands.PropertiesDiffCommand;
import io.osdf.common.SettingsFile;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.actions.init.configs.ConfigsUpdater.configsUpdater;
import static io.osdf.actions.init.configs.fetch.ConfigsFetcher.fetcher;
import static io.osdf.core.local.microconfig.MicroConfig.microConfig;
import static io.osdf.core.local.microconfig.property.PropertySetter.propertySetter;

@RequiredArgsConstructor
public class ConfigsApiImpl implements ConfigsApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ConfigsApi configsApi(OsdfPaths paths, ClusterCli cli) {
        return new ConfigsApiImpl(paths, cli);
    }

    @Override
    public void propertiesDiff(List<String> components) {
        new PropertiesDiffCommand(paths).show(components);
    }

    @Override
    public void changeVersion(String component, String version) {
        propertySetter().setIfNecessary(paths.projectVersionPath(), "project.version", version);

        String env = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getEnv();
        microConfig(env, paths).generateSingleComponent(component);
    }

    @Override
    public void group(String group) {
        SettingsFile<ConfigsSettings> file = settingsFile(ConfigsSettings.class, paths.settings().configs());
        file.getSettings().setGroup("ALL".equals(group) ? null : group);
        file.save();
    }

    @Override
    public void pull() {
        configsUpdater(paths, cli).fetch();
    }

    @Override
    public void configVersion(String configVersion) {
        ConfigsSource configsSource = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getConfigsSource();
        fetcher(configsSource, paths).setConfigVersion(configVersion);

        configsUpdater(paths, cli).fetch();
    }
}
