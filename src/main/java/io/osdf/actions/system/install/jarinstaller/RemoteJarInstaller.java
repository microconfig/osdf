package io.osdf.actions.system.install.jarinstaller;

import io.osdf.actions.system.update.UpdateSettings;
import io.osdf.common.Credentials;
import io.osdf.common.nexus.NexusArtifact;
import io.osdf.settings.OsdfConfig;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersion;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.nexus.NexusArtifact.nexusArtifact;
import static io.osdf.common.nexus.NexusClient.nexusClient;
import static io.osdf.common.utils.FileUtils.move;
import static io.osdf.settings.OsdfConfig.osdfConfig;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class RemoteJarInstaller implements JarInstaller {
    private final OsdfVersion version;
    private final OsdfPaths paths;

    public static RemoteJarInstaller jarInstaller(OsdfVersion version, OsdfPaths paths) {
        return new RemoteJarInstaller(version, paths);
    }

    @Override
    public OsdfVersion version() {
        return version;
    }

    @Override
    public void prepare() {
        downloadJar(version);
    }

    @Override
    public void replace() {
        move(of(paths.tmp() + "/osdf.jar"), of(paths.root() + "/osdf.jar"));
    }

    private void downloadJar(OsdfVersion version) {
        OsdfConfig downloadProperties = osdfConfig(paths);
        Credentials credentials = settingsFile(UpdateSettings.class, paths.settings().update()).getSettings().getCredentials();

        String url = downloadProperties.url();
        NexusArtifact nexusArtifact = nexusArtifact(downloadProperties.group(), downloadProperties.artifact(), version.toString(), "jar");
        nexusClient(url, credentials).download(nexusArtifact, of(paths.tmp() + "/osdf.jar"));
    }
}
