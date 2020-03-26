package io.microconfig.osdf.configfetcher;

import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.Credentials;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.nexus.NexusClient.nexusClient;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class NexusConfigFetcher implements ConfigFetcherStrategy {
    private static final Path CONFIGS_TMP_DOWNLOAD_PATH = of("/tmp/configs.zip");

    private final String nexusUrl;
    private final Credentials credentials;
    private final NexusArtifact configsNexusArtifact;

    public static NexusConfigFetcher nexusFetcher(String nexusUrl, Credentials credentials, NexusArtifact configsNexusArtifact) {
        return new NexusConfigFetcher(nexusUrl, credentials, configsNexusArtifact);
    }

    @Override
    public void fetchConfigs(String configVersion, Path destination) {
        configsNexusArtifact.setVersion(configVersion);
        nexusClient(nexusUrl, credentials).download(configsNexusArtifact, CONFIGS_TMP_DOWNLOAD_PATH);
        execute("rm -rf " + destination);
        execute("unzip " + CONFIGS_TMP_DOWNLOAD_PATH + " -d " + destination);
    }
}
