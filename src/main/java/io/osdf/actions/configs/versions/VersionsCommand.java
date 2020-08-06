package io.osdf.actions.configs.versions;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.init.configs.ConfigsUpdater.configsUpdater;
import static io.osdf.actions.init.configs.fetch.ConfigsFetcher.fetcher;
import static io.osdf.actions.init.configs.postprocess.AppPostProcessor.componentPostProcessor;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.local.component.loader.ComponentsLoaderImpl.componentsLoader;
import static io.osdf.core.local.microconfig.MicroConfig.microConfig;
import static io.osdf.core.local.microconfig.property.PropertySetter.propertySetter;

@RequiredArgsConstructor
public class VersionsCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static VersionsCommand versionsCommand(OsdfPaths paths, ClusterCli cli) {
        return new VersionsCommand(paths, cli);
    }

    public void setVersions(String configVersion, String projectVersion, String app) {
        if (app != null) {
            setVersionsForSingleApp(configVersion, projectVersion, app);
        } else {
            setVersionsForAllApps(configVersion, projectVersion);
        }
    }

    private void setVersionsForAllApps(String configVersion, String projectVersion) {
        setConfigVersion(configVersion);
        configsUpdater(paths, cli)
                .setConfigsParameters(null, projectVersion)
                .fetch();
    }

    private void setVersionsForSingleApp(String configVersion, String projectVersion, String app) {
        propertySetter().setIfNecessary(paths.projectVersionPath(), "project.version", projectVersion);
        propertySetter().setIfNecessary(paths.configVersionPath(), "config.version", configVersion);

        String env = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getEnv();
        microConfig(env, paths).generateSingleComponent(app);

        ComponentDir componentDir = componentsLoader()
                .loadOne(app, componentsFinder(paths.componentsPath()));
        componentPostProcessor().process(componentDir);
    }

    private void setConfigVersion(String configVersion) {
        if (configVersion == null) return;
        ConfigsSource configsSource = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getConfigsSource();
        fetcher(configsSource, paths).setConfigVersion(configVersion);
    }
}
