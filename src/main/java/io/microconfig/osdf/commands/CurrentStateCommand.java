package io.microconfig.osdf.commands;


import io.microconfig.osdf.configs.ConfigsSettings;
import io.microconfig.osdf.configs.ConfigsSource;
import io.microconfig.osdf.openshift.OpenShiftCredentials;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersionFile;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.configfetcher.ConfigsFetcher.fetcher;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;


@RequiredArgsConstructor
public class CurrentStateCommand {
    private final OSDFPaths paths;

    public void show() {
        osdf();
        configSource();
        openshift();
        configs();
    }

    private void osdf() {
        announce("OSDF");
        info(settingsFile(OSDFVersionFile.class, paths.settings().osdf()).getSettings().toString().strip());
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

    private void openshift() {
        announce("OpenShift");
        info(settingsFile(OpenShiftCredentials.class, paths.settings().openshift()).getSettings().toString().strip());
    }

    private void configs() {
        announce("Configs");
        info(settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().toString().strip());
    }
}
