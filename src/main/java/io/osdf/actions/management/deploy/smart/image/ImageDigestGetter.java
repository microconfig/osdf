package io.osdf.actions.management.deploy.smart.image;

import io.osdf.common.Credentials;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.StringUtils.withQuotes;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class ImageDigestGetter {
    private final Credentials credentials;
    private final String host;
    private final String imagePath;

    public static ImageDigestGetter imageDigestGetter(String imageUrl, OsdfPaths paths) {
        String[] hostAndPath = imageUrl
                .replaceFirst("http://", "")
                .replaceFirst("https://", "")
                .replaceFirst("/", "---")
                .split("---");
        Credentials credentials = settingsFile(RegistryCredentials.class, paths.settings().registryCredentials())
                .getSettings()
                .getForUrl(hostAndPath[0]);
        if (credentials == null) throw new OSDFException("No registry credentials found for " + hostAndPath[0]);
        return new ImageDigestGetter(credentials, hostAndPath[0], hostAndPath[1]);
    }

    public String get() {
        String output = execute("curl -k " +
                "-H \"Authorization: Bearer " + token() + "\" " +
                "-H \"Accept: application/vnd.docker.distribution.manifest.v2+json\" " +
                manifestUrl() + " -D -");
        String digestHeader = stream(output.split("\n"))
                .filter(line -> line.contains("Docker-Content-Digest"))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Digest was not for " + imagePath))
                .strip();
        return digestHeader.split(" ")[1];
    }

    private String token() {
        String output = execute("curl -k -u " + withQuotes(credentials.getCredentialsString()) + " https://" + host + "/v2/token");
        Matcher matcher = compile(".*\"(DockerToken.*)\".*").matcher(output);
        if (!matcher.matches()) throw new OSDFException("Unknown registry token format");
        return matcher.group(1);
    }

    private String manifestUrl() {
        String[] pathAndTag = imagePath.split(":");
        return "https://" + host + "/v2/" + pathAndTag[0] + "/manifests/" + pathAndTag[1];
    }
}
