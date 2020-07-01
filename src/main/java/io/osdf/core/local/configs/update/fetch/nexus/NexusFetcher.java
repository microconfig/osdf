package io.osdf.core.local.configs.update.fetch.nexus;

import io.osdf.core.local.configs.update.fetch.ConfigsFetcherStrategy;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.nexus.NexusClient.nexusClient;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.YamlUtils.dump;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class NexusFetcher implements ConfigsFetcherStrategy {
    private static final Path CONFIGS_TMP_DOWNLOAD_PATH = of("/tmp/configs.zip");

    private final NexusFetcherSettings settings;
    private final Path settingsPath;

    public static NexusFetcher nexusFetcher(Path settingsPath) {
        NexusFetcherSettings settings = settingsFile(NexusFetcherSettings.class, settingsPath).getSettings();
        return new NexusFetcher(settings, settingsPath);
    }

    @Override
    public boolean verifyAndLogErrors() {
        return settings.verifyAndLogErrors();
    }

    @Override
    public void fetch(Path destination) {
        nexusClient(settings.getUrl(), settings.getCredentials()).download(settings.getArtifact(), CONFIGS_TMP_DOWNLOAD_PATH);
        execute("rm -rf " + destination);
        execute("unzip " + CONFIGS_TMP_DOWNLOAD_PATH + " -d " + destination);
    }

    @Override
    public void setConfigVersion(String configVersion) {
        settings.getArtifact().setVersion(configVersion);
        dump(settings, settingsPath);
    }

    @Override
    public String getConfigVersion() {
        return settings.getArtifact().getVersion();
    }

    @Override
    public String toString() {
        return "Type: nexus" + "\n" + settings;
    }
}
