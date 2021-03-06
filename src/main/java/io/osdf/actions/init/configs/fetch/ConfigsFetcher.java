package io.osdf.actions.init.configs.fetch;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.init.configs.fetch.git.GitFetcher.gitFetcher;
import static io.osdf.actions.init.configs.fetch.local.LocalFetcher.localFetcher;
import static io.osdf.actions.init.configs.fetch.nexus.NexusFetcher.nexusFetcher;
import static io.osdf.common.utils.FileUtils.delete;

@RequiredArgsConstructor
public class ConfigsFetcher {
    private final ConfigsSource configsSource;
    private final OsdfPaths paths;

    public static ConfigsFetcher fetcher(ConfigsSource configsSource, OsdfPaths paths) {
        return new ConfigsFetcher(configsSource, paths);
    }

    public void fetchConfigs() {
        ConfigsFetcherStrategy fetcherStrategy = fetchingStrategy();
        if (!fetcherStrategy.verifyAndLogErrors()) throw new OSDFException("Incomplete configs source configuration");

        delete(paths.configsDownloadPath());
        fetcherStrategy.fetch(paths.configsDownloadPath());
    }

    public void setConfigVersion(String version) {
        fetchingStrategy().setConfigVersion(version);
    }

    public String getConfigVersion() {
        return fetchingStrategy().getConfigVersion();
    }

    private ConfigsFetcherStrategy fetchingStrategy() {
        switch (configsSource) {
            case GIT: return gitFetcher(paths.settings().gitFetcher());
            case LOCAL: return localFetcher(paths.settings().localFetcher());
            case NEXUS: return nexusFetcher(paths.settings().nexusFetcher());
        }
        throw new OSDFException("No fetch strategy found");
    }

    @Override
    public String toString() {
        return fetchingStrategy().toString();
    }
}
