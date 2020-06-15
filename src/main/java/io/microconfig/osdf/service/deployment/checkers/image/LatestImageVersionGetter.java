package io.microconfig.osdf.service.deployment.checkers.image;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;

import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.info;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class LatestImageVersionGetter {
    private final Credentials credentials;
    private final String host;
    private final String imagePath;
    private final boolean configured;

    public static LatestImageVersionGetter latestImageVersionGetter(ServiceFiles files, OSDFPaths paths) {
        String[] hostAndPath = getString(loadFromPath(files.getPath("deploy.yaml")), "image", "url")
                .replaceFirst("http://", "")
                .replaceFirst("https://", "")
                .replaceFirst("/", "---")
                .split("---");
        Credentials credentials = settingsFile(RegistryCredentials.class, paths.settings().registryCredentials())
                .getSettings()
                .getForUrl(hostAndPath[0]);
        if (credentials == null) {
            info("No credentials found for " + hostAndPath[0]);
            return new LatestImageVersionGetter(null, null, null, false);
        }
        return new LatestImageVersionGetter(credentials, hostAndPath[0], hostAndPath[1], true);
    }

    public String get() {
        if (!configured) return "fake";
        return imageId();
    }

    private String token() {
        String output = execute("curl -k -u " + credentials.getCredentialsString() + " https://" + host + "/v2/token");
        Matcher matcher = compile(".*\"(DockerToken.*)\".*").matcher(output);
        if (!matcher.matches()) throw new OSDFException("Unknown registry token format");
        return matcher.group(1);
    }

    private String imageId() {
        String output = execute("curl -k " +
                "-H \"Authorization: Bearer " + token() + "\" " +
                "-H \"Accept: application/vnd.docker.distribution.manifest.v2+json\" " +
                manifestUrl() + " -D -");
        String digestHeader = stream(output.split("\n"))
                .filter(line -> line.contains("Docker-Content-Digest"))
                .findFirst()
                .orElse("Docker-Content-Digest: notfound")
                .strip();
        return digestHeader.split(" ")[1];
    }

    private String manifestUrl() {
        String[] pathAndTag = imagePath.split(":");
        return "https://" + host + "/v2/" + pathAndTag[0] + "/manifests/" + pathAndTag[1];
    }
}
