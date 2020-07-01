package io.osdf.actions.management.deploy.smart.hash.image;

import io.osdf.common.Credentials;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.common.utils.YamlUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.StringUtils.withQuotes;
import static io.osdf.common.utils.YamlUtils.*;
import static io.microconfig.utils.Logger.info;
import static io.microconfig.utils.Logger.warn;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class LatestImageVersionGetter {
    private final Credentials credentials;
    private final String host;
    private final String imagePath;
    private final boolean configured;

    public static LatestImageVersionGetter latestImageVersionGetter(ServiceFiles files, OsdfPaths paths) {
        String[] hostAndPath = imageUrl(files)
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

    @SuppressWarnings("unchecked")
    private static String imageUrl(ServiceFiles files) {
        List<Object> containers = YamlUtils.get(loadFromPath(files.getPath("mainResource")), "spec.template.spec.containers");
        return getString((Map<String, Object>) containers.get(0), "image");
    }

    public String get() {
        if (!configured) return "fake";
        try {
            return imageId();
        } catch (RuntimeException e) {
            warn("Error querying " + host + ". Smart deploy might not work.");
            return "fake";
        }
    }

    private String token() {
        String output = execute("curl -k -u " + withQuotes(credentials.getCredentialsString()) + " https://" + host + "/v2/token");
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
