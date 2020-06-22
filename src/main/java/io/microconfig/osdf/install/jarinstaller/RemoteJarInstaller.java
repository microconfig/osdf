package io.microconfig.osdf.install.jarinstaller;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.install.UpdateSettings;
import io.microconfig.osdf.microconfig.properties.OSDFDownloadProperties;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.microconfig.properties.OSDFDownloadProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.nexus.NexusArtifact.nexusArtifact;
import static io.microconfig.osdf.nexus.NexusClient.nexusClient;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class RemoteJarInstaller implements JarInstaller {
    private final OSDFVersion version;
    private final OSDFPaths paths;

    public static RemoteJarInstaller jarInstaller(OSDFVersion version, OSDFPaths paths) {
        return new RemoteJarInstaller(version, paths);
    }

    @Override
    public OSDFVersion version() {
        return version;
    }

    @Override
    public void prepare() {
        downloadJar(version);
//        execute("cp /tmp/osdf.jar " + paths.tmp() + "/osdf.jar");
    }

    @Override
    public void replace() {
        execute("mv " + paths.tmp() + "/osdf.jar " + paths.root() + "/osdf.jar");
    }

    private void downloadJar(OSDFVersion version) {
        OSDFDownloadProperties downloadProperties = properties(propertyGetter(paths));
        Credentials credentials = settingsFile(UpdateSettings.class, paths.settings().update()).getSettings().getCredentials();

        String url = downloadProperties.url();
        NexusArtifact nexusArtifact = nexusArtifact(downloadProperties.group(), downloadProperties.artifact(), version.toString(), "jar");
        nexusClient(url, credentials).download(nexusArtifact, of(paths.tmp() + "/osdf.jar"));
    }
}
