package io.osdf.actions.init.configs.fetch.nexus;

import io.osdf.actions.init.configs.fetch.ConfigsFetcherStrategy;
import io.osdf.common.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.nexus.NexusClient.nexusClient;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class NexusFetcher implements ConfigsFetcherStrategy {
    private static final Path CONFIGS_TMP_DOWNLOAD_PATH = of("/tmp/configs.zip");

    private final SettingsFile<NexusFetcherSettings> file;

    public static NexusFetcher nexusFetcher(Path settingsPath) {
        return new NexusFetcher(settingsFile(NexusFetcherSettings.class, settingsPath));
    }

    @Override
    public boolean verifyAndLogErrors() {
        return file.getSettings().verifyAndLogErrors();
    }

    @Override
    public void fetch(Path destination) {
        NexusFetcherSettings settings = file.getSettings();
        nexusClient(settings.getUrl(), settings.getCredentials()).download(settings.getArtifact(), CONFIGS_TMP_DOWNLOAD_PATH);
        execute("rm -rf " + destination);
        execute("unzip " + CONFIGS_TMP_DOWNLOAD_PATH + " -d " + destination);
    }

    @Override
    public void setConfigVersion(String configVersion) {
        file.getSettings().getArtifact().setVersion(configVersion);
        file.save();
    }

    @Override
    public String getConfigVersion() {
        return file.getSettings().getArtifact().getVersion();
    }

    @Override
    public String toString() {
        return "Type: nexus" + "\n" + file.getSettings();
    }
}
