package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.InitializationApi;
import io.cluster.old.cluster.context.ClusterContextSettings;
import io.cluster.old.cluster.context.ClusterType;
import io.cluster.old.cluster.kubernetes.KubernetesSettings;
import io.cluster.old.cluster.openshift.OpenShiftCredentials;
import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.configfetcher.git.GitFetcherSettings;
import io.microconfig.osdf.configfetcher.local.LocalFetcherSettings;
import io.microconfig.osdf.configfetcher.nexus.NexusFetcherSettings;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.osdf.settings.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.checkers.image.RegistryCredentials;
import io.microconfig.osdf.settings.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.cluster.old.cluster.context.ClusterType.KUBERNETES;
import static io.cluster.old.cluster.context.ClusterType.OPENSHIFT;
import static io.cluster.old.cluster.openshift.OpenShiftCLI.oc;
import static io.microconfig.osdf.configs.ConfigsSource.*;
import static io.microconfig.osdf.configs.ConfigsUpdater.configsUpdater;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class InitializationApiImpl implements InitializationApi {
    private final OSDFPaths paths;

    public static InitializationApi initializationApi(OSDFPaths paths) {
        return new InitializationApiImpl(paths);
    }

    @Override
    public void gitConfigs(String url, String branchOrTag) {
        updateGitConfigsSettings(url, branchOrTag);
        configsUpdater(paths).setConfigsSource(GIT);
    }

    @Override
    public void nexusConfigs(String url, NexusArtifact artifact, Credentials credentials) {
        updateNexusConfigsSettings(url, artifact, credentials);
        configsUpdater(paths).setConfigsSource(NEXUS);
    }

    @Override
    public void localConfigs(Path path, String version) {
        updateLocalConfigsSettings(path, version);
        configsUpdater(paths).setConfigsSource(LOCAL);
    }

    @Override
    public void openshift(Credentials credentials, String token, Boolean loginImmediately) {
        if (credentials == null && token == null) throw new OSDFException("Provide credentials (-c) or token (-t) parameter");
        if (credentials != null && token != null) throw new OSDFException("Choose only one authentication type");
        updateOpenShiftSettings(credentials, token);
        updateClusterContextSettings(OPENSHIFT);
        if (loginImmediately) {
            oc(paths).login();
        }
    }

    @Override
    public void kubernetes(Credentials credentials) {
        updateKubernetesSettings(credentials);
        updateClusterContextSettings(KUBERNETES);
    }

    @Override
    public void configs(String env, String projVersion) {
        configsUpdater(paths).setConfigsParameters(env, projVersion);
    }

    @Override
    public void registry(String url, Credentials credentials) {
        SettingsFile<RegistryCredentials> file = settingsFile(RegistryCredentials.class, paths.settings().registryCredentials());
        file.getSettings().add(url, credentials);
        file.save();
    }

    private void updateGitConfigsSettings(String url, String branchOrTag) {
        SettingsFile<GitFetcherSettings> settingsFile = settingsFile(GitFetcherSettings.class, paths.settings().gitFetcher());
        settingsFile.setIfNotNull(GitFetcherSettings::setUrl, url);
        settingsFile.setIfNotNull(GitFetcherSettings::setBranchOrTag, branchOrTag);
        settingsFile.save();
    }

    private void updateNexusConfigsSettings(String url, NexusArtifact artifact, Credentials credentials) {
        SettingsFile<NexusFetcherSettings> settingsFile = settingsFile(NexusFetcherSettings.class, paths.settings().nexusFetcher());
        settingsFile.setIfNotNull(NexusFetcherSettings::setUrl, url);
        settingsFile.setIfNotNull(NexusFetcherSettings::setArtifact, artifact);
        settingsFile.setIfNotNull(NexusFetcherSettings::setCredentials, credentials);
        settingsFile.save();
    }

    private void updateLocalConfigsSettings(Path path, String version) {
        SettingsFile<LocalFetcherSettings> settingsFile = settingsFile(LocalFetcherSettings.class, paths.settings().localFetcher());
        settingsFile.setIfNotNull(LocalFetcherSettings::setPath, path == null ? null : path.toString());
        settingsFile.setIfNotNull(LocalFetcherSettings::setVersion, version);
        settingsFile.save();
    }

    private void updateClusterContextSettings(ClusterType type) {
        SettingsFile<ClusterContextSettings> contextFile = settingsFile(ClusterContextSettings.class, paths.settings().clusterContext());
        contextFile.getSettings().setType(type);
        contextFile.save();
    }

    private void updateOpenShiftSettings(Credentials credentials, String token) {
        SettingsFile<OpenShiftCredentials> credentialsFile = settingsFile(OpenShiftCredentials.class, paths.settings().openshift());
        credentialsFile.getSettings().setCredentials(credentials);
        credentialsFile.getSettings().setToken(token);
        credentialsFile.save();
    }

    private void updateKubernetesSettings(Credentials credentials) {
        SettingsFile<KubernetesSettings> credentialsFile = settingsFile(KubernetesSettings.class, paths.settings().kubernetes());
        credentialsFile.getSettings().setCredentials(credentials);
        credentialsFile.save();
    }
}
