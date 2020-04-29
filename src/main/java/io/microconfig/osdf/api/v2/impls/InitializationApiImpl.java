package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.InitializationApi;
import io.microconfig.osdf.configfetcher.git.GitFetcherSettings;
import io.microconfig.osdf.configfetcher.local.LocalFetcherSettings;
import io.microconfig.osdf.configfetcher.nexus.NexusFetcherSettings;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.openshift.OpenShiftCredentials;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.settings.SettingsFile;
import io.microconfig.osdf.state.Credentials;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.configs.ConfigsSource.*;
import static io.microconfig.osdf.configs.ConfigsUpdater.configsUpdater;
import static io.microconfig.osdf.paths.SettingsPaths.settingsPaths;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class InitializationApiImpl implements InitializationApi {
    private final OSDFPaths paths;

    public static InitializationApi initializationApi(OSDFPaths paths) {
        return new InitializationApiImpl(paths);
    }

    @Override
    public void gitConfigs(String url, String branchOrTag) {
        SettingsFile<GitFetcherSettings> settingsFile = settingsFile(GitFetcherSettings.class, settingsPaths(paths.settingsRootPath()).gitFetcher());
        settingsFile.setIfNotNull(GitFetcherSettings::setUrl, url);
        settingsFile.setIfNotNull(GitFetcherSettings::setBranchOrTag, branchOrTag);
        settingsFile.save();

        configsUpdater(paths).setConfigsSource(GIT);
    }

    @Override
    public void nexusConfigs(String url, NexusArtifact artifact, Credentials credentials) {
        SettingsFile<NexusFetcherSettings> settingsFile = settingsFile(NexusFetcherSettings.class, settingsPaths(paths.settingsRootPath()).nexusFetcher());
        settingsFile.setIfNotNull(NexusFetcherSettings::setUrl, url);
        settingsFile.setIfNotNull(NexusFetcherSettings::setArtifact, artifact);
        settingsFile.setIfNotNull(NexusFetcherSettings::setCredentials, credentials);
        settingsFile.save();

        configsUpdater(paths).setConfigsSource(NEXUS);
    }

    @Override
    public void localConfigs(Path path) {
        SettingsFile<LocalFetcherSettings> settingsFile = settingsFile(LocalFetcherSettings.class, settingsPaths(paths.settingsRootPath()).localFetcher());
        settingsFile.setIfNotNull(LocalFetcherSettings::setPath, path.toString());
        settingsFile.save();

        configsUpdater(paths).setConfigsSource(LOCAL);
    }

    @Override
    public void openshift(Credentials credentials, String token) {
        if (credentials == null && token == null) throw new OSDFException("Provide credentials (-c) or token (-t) parameter");
        SettingsFile<OpenShiftCredentials> settingsFile = settingsFile(OpenShiftCredentials.class, settingsPaths(paths.settingsRootPath()).openshift());
        settingsFile.setIfNotNull(OpenShiftCredentials::setCredentials, credentials);
        settingsFile.setIfNotNull(OpenShiftCredentials::setToken, token);
        settingsFile.save();
    }

    @Override
    public void configs(String env, String projVersion) {
        configsUpdater(paths).setConfigsParameters(env, projVersion);
    }
}
