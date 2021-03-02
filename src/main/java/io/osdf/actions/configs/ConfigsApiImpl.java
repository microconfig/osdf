package io.osdf.actions.configs;

import io.osdf.actions.configs.diff.DiffCommand;
import io.osdf.common.SettingsFile;
import io.osdf.core.application.core.Application;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.configs.requiredSecrets.RequiredAppSecrets.requiredAppSecrets;
import static io.osdf.actions.configs.versions.VersionsCommand.versionsCommand;
import static io.osdf.actions.init.configs.ConfigsUpdater.configsUpdater;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static java.lang.String.join;

@RequiredArgsConstructor
public class ConfigsApiImpl implements ConfigsApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ConfigsApi configsApi(OsdfPaths paths, ClusterCli cli) {
        return new ConfigsApiImpl(paths, cli);
    }

    @Override
    public void pull() {
        configsUpdater(paths, cli).fetch();
    }

    @Override
    public void env(String env) {
        configsUpdater(paths, cli)
                .setConfigsParameters(env, null, null)
                .buildConfigs();
    }

    @Override
    public void group(String group) {
        SettingsFile<ConfigsSettings> file = settingsFile(ConfigsSettings.class, paths.settings().configs());
        file.getSettings().setGroup("ALL".equals(group) ? null : group);
        file.save();
    }

    @Override
    public void versions(String configVersion, String projectVersion, String app) {
        versionsCommand(paths, cli).setVersions(configVersion, projectVersion, app);
    }

    @Override
    public void diff(List<String> components) {
        new DiffCommand(paths).show(components);
    }

    @Override
    public void requiredSecrets(List<String> components) {
        List<Application> apps = activeRequiredAppsLoader(paths, components).load(all(cli));
        List<String> secretNames = requiredAppSecrets().listFor(apps);
        info(join(", ", secretNames));
    }
}
