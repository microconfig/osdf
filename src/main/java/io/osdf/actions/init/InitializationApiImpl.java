package io.osdf.actions.init;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.connection.context.ClusterContextSettings;
import io.osdf.core.connection.context.ClusterType;
import io.osdf.core.connection.cli.kubernetes.KubernetesSettings;
import io.osdf.core.connection.cli.openshift.OpenShiftCredentials;
import io.osdf.common.Credentials;
import io.osdf.core.local.configs.update.fetch.git.GitFetcherSettings;
import io.osdf.core.local.configs.update.fetch.local.LocalFetcherSettings;
import io.osdf.core.local.configs.update.fetch.nexus.NexusFetcherSettings;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.nexus.NexusArtifact;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.actions.management.deploy.smart.hash.image.RegistryCredentials;
import io.osdf.common.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.core.connection.context.ClusterType.KUBERNETES;
import static io.osdf.core.connection.context.ClusterType.OPENSHIFT;
import static io.osdf.core.connection.cli.openshift.OpenShiftCli.oc;
import static io.osdf.core.local.configs.ConfigsSource.*;
import static io.osdf.core.local.configs.update.ConfigsUpdater.configsUpdater;
import static io.osdf.common.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class InitializationApiImpl implements InitializationApi {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static InitializationApi initializationApi(OsdfPaths paths, ClusterCli cli) {
        return new InitializationApiImpl(paths, cli);
    }

    @Override
    public void gitConfigs(String url, String branchOrTag) {
        updateGitConfigsSettings(url, branchOrTag);
        configsUpdater(paths, cli).setConfigsSource(GIT);
    }

    @Override
    public void nexusConfigs(String url, NexusArtifact artifact, Credentials credentials) {
        updateNexusConfigsSettings(url, artifact, credentials);
        configsUpdater(paths, cli).setConfigsSource(NEXUS);
    }

    @Override
    public void localConfigs(Path path, String version) {
        updateLocalConfigsSettings(path, version);
        configsUpdater(paths, cli).setConfigsSource(LOCAL);
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
        configsUpdater(paths, cli).setConfigsParameters(env, projVersion);
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
