package io.microconfig.osdf.configfetcher;

import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.configfetcher.GitFetcher.gitFetcher;
import static io.microconfig.osdf.configfetcher.LocalFetcher.localFetcher;
import static io.microconfig.osdf.configfetcher.NexusConfigFetcher.nexusFetcher;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ConfigFetcher {
    private final OSDFState state;
    private final Path configsDownloadPath;

    public static ConfigFetcher fetcher(OSDFState state, Path configsDownloadPath) {
        return new ConfigFetcher(state, configsDownloadPath);
    }

    public void fetchConfigs() {
        fetchingStrategy().fetchConfigs(state.getConfigVersion(), configsDownloadPath);
    }

    private ConfigFetcherStrategy fetchingStrategy() {
        switch (state.getConfigSource()) {
            case GIT: return gitFetcher(state.getGitUrl());
            case LOCAL: return localFetcher(of(state.getLocalConfigs()));
            case NEXUS: return nexusFetcher(state.getNexusUrl(), state.getNexusCredentials(), state.getConfigsNexusArtifact());
        }
        throw new RuntimeException("No fetch strategy found");
    }
}
