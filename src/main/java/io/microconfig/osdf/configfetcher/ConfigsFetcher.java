package io.microconfig.osdf.configfetcher;

import io.microconfig.osdf.configs.ConfigsSource;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.paths.SettingsPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.configfetcher.git.GitFetcher.gitFetcher;
import static io.microconfig.osdf.configfetcher.local.LocalFetcher.localFetcher;
import static io.microconfig.osdf.configfetcher.nexus.NexusFetcher.nexusFetcher;
import static io.microconfig.osdf.paths.SettingsPaths.settingsPaths;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;

@RequiredArgsConstructor
public class ConfigsFetcher {
    private final ConfigsSource configsSource;
    private final OSDFPaths paths;

    public static ConfigsFetcher fetcher(ConfigsSource configsSource, OSDFPaths paths) {
        return new ConfigsFetcher(configsSource, paths);
    }

    public void fetchConfigs() {
        execute("rm -rf " + paths.configsDownloadPath());
        fetchingStrategy().fetch(paths.configsDownloadPath());
    }

    public void setConfigVersion(String version) {
        fetchingStrategy().setConfigVersion(version);
    }

    public String getConfigVersion() {
        return fetchingStrategy().getConfigVersion();
    }

    private ConfigsFetcherStrategy fetchingStrategy() {
        SettingsPaths settingsPaths = settingsPaths(paths.settingsRootPath());
        switch (configsSource) {
            case GIT: return gitFetcher(settingsPaths.gitFetcher());
            case LOCAL: return localFetcher(settingsPaths.localFetcher());
            case NEXUS: return nexusFetcher(settingsPaths.nexusFetcher());
        }
        throw new RuntimeException("No fetch strategy found");
    }
}
