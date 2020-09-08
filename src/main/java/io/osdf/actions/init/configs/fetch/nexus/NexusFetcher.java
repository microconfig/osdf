package io.osdf.actions.init.configs.fetch.nexus;

import io.osdf.actions.init.configs.fetch.ConfigsFetcherStrategy;
import io.osdf.common.SettingsFile;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import org.zeroturnaround.zip.ZipException;

import java.nio.file.Path;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.nexus.NexusClient.nexusClient;
import static io.osdf.common.utils.FileUtils.createTempDirectory;
import static io.osdf.common.utils.FileUtils.delete;
import static java.nio.file.Path.of;
import static org.zeroturnaround.zip.ZipUtil.unpack;

@RequiredArgsConstructor
public class NexusFetcher implements ConfigsFetcherStrategy {
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
        Path configsDownloadPath = of(createTempDirectory("configs") + "/configs.zip");

        NexusFetcherSettings settings = file.getSettings();
        nexusClient(settings.getUrl(), settings.getCredentials()).download(settings.getArtifact(), configsDownloadPath);

        delete(destination);

        unzip(configsDownloadPath, destination);
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

    private void unzip(Path configsDownloadPath, Path destination) {
        try {
            unpack(configsDownloadPath.toFile(), destination.toFile());
        } catch (ZipException e) {
            throw new OSDFException("Can't extract zip configs at " + configsDownloadPath + ": " + e.getMessage());
        }
    }
}
