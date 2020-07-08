package io.osdf.actions.system.state;

import io.osdf.core.connection.cli.kubernetes.KubernetesSettings;
import io.osdf.core.connection.cli.openshift.OpenShiftCredentials;
import io.osdf.core.connection.context.ClusterContextSettings;
import io.osdf.core.connection.context.ClusterType;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersionFile;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.init.configs.fetch.ConfigsFetcher.fetcher;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.core.connection.context.ClusterType.OPENSHIFT;

@RequiredArgsConstructor
public class CurrentStateCommand {
    private final OsdfPaths paths;

    public void show() {
        osdf();
        configSource();
        cluster();
        configs();
    }

    private void osdf() {
        announce("OSDF");
        info(settingsFile(OsdfVersionFile.class, paths.settings().osdf()).getSettings().toString().strip());
    }

    private void configSource() {
        announce("Config source");
        ConfigsSource configsSource = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getConfigsSource();
        if (configsSource == null) {
            info("Not configured");
            return;
        }

        info(fetcher(configsSource, paths).toString().strip());
    }

    private void cluster() {
        announce("Cluster");
        ClusterType type = settingsFile(ClusterContextSettings.class, paths.settings().clusterContext()).getSettings().getType();
        if (type == null) {
            info("Not configured");
            return;
        }
        info("Type: " + type);
        if (type == OPENSHIFT) {
            info(settingsFile(OpenShiftCredentials.class, paths.settings().openshift()).getSettings().toString().strip());
        } else {
            info(settingsFile(KubernetesSettings.class, paths.settings().kubernetes()).getSettings().toString().strip());
        }
    }

    private void configs() {
        announce("Configs");
        info(settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().toString().strip());
    }
}