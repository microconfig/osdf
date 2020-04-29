package io.microconfig.osdf.configfetcher.nexus;

import io.microconfig.osdf.configfetcher.ConfigsFetcherStrategy;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.nexus.NexusClient.nexusClient;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.YamlUtils.dump;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class NexusFetcher implements ConfigsFetcherStrategy {
    private static final Path CONFIGS_TMP_DOWNLOAD_PATH = of("/tmp/configs.zip");

    private final NexusFetcherSettings settings;
    private final Path settingsPath;

    public static NexusFetcher nexusFetcher(Path settingsPath) {
        NexusFetcherSettings settings = settingsFile(NexusFetcherSettings.class, settingsPath).getSettings();
        if (!settings.verifyAndLogErrors()) throw new OSDFException("Incomplete configs source configuration");
        return new NexusFetcher(settings, settingsPath);
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
}
