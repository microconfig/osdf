package io.microconfig.osdf.service.deployment.checkers.image;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class LatestImageVersionGetter {
    private final Credentials credentials;
    private final String host;
    private final String imagePath;

    public static LatestImageVersionGetter latestImageVersionGetter(Credentials credentials, String host, String imagePath) {
        return new LatestImageVersionGetter(credentials, host, imagePath);
    }

    public String get() {
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
