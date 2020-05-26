package io.microconfig.osdf.service.deployment.checkers.image;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.checkers.image.LatestImageVersionGetter.latestImageVersionGetter;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.info;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ImageVersionChecker {
    private final ServiceDeployment deployment;
    private final ServiceFiles serviceFiles;
    private final OSDFPaths paths;

    public static ImageVersionChecker imageVersionChecker(ServiceDeployment deployment, ServiceFiles serviceFiles, OSDFPaths paths) {
        return new ImageVersionChecker(deployment, serviceFiles, paths);
    }

    public boolean isLatest() {
        if (!deployment.version().toLowerCase().endsWith("-snapshot")) return true;

        List<String> currentVersions = currentVersions();
        String latestVersion = latestVersion();
        if (latestVersion == null) return false;
        return currentVersions.stream().allMatch(version -> version.equals(latestVersion));
    }

    private String latestVersion() {
        String[] hostAndPath = getString(loadFromPath(serviceFiles.getPath("deploy.yaml")), "image", "url")
                .replaceFirst("http://", "")
                .replaceFirst("https://", "")
                .replaceFirst("/", "---")
                .split("---");
        Credentials credentials = settingsFile(RegistryCredentials.class, paths.settings().registryCredentials())
                .getSettings()
                .getForUrl(hostAndPath[0]);
        if (credentials == null) {
            info("No credentials found for " + hostAndPath[0]);
            return null;
        }
        return latestImageVersionGetter(credentials, hostAndPath[0], hostAndPath[1]).get();
    }

    private List<String> currentVersions() {
        return deployment.pods()
                .stream()
                .map(Pod::imageId)
                .collect(toUnmodifiableList());
    }
}
