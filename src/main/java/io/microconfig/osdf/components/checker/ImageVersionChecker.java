package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.checker.LatestImageVersionGetter.latestImageVersionGetter;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.info;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ImageVersionChecker {
    private final DeploymentComponent component;
    private final OSDFPaths paths;

    public static ImageVersionChecker imageVersionChecker(DeploymentComponent component, OSDFPaths paths) {
        return new ImageVersionChecker(component, paths);
    }

    public boolean isLatest() {
        if (!component.getVersion().toLowerCase().endsWith("-snapshot")) return true;

        List<String> currentVersions = currentVersions();
        String latestVersion = latestVersion();
        if (latestVersion == null) return false;
        return currentVersions.stream().allMatch(version -> version.equals(latestVersion));
    }

    private String latestVersion() {
        String[] hostAndPath = getString(loadFromPath(of(component.getConfigDir() + "/deploy.yaml")), "image", "url")
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
        return component.pods()
                .stream()
                .map(Pod::imageId)
                .collect(toUnmodifiableList());
    }
}
