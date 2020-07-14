package io.osdf.actions.management.deploy.smart.image.digest;

import io.osdf.actions.management.deploy.smart.image.RegistryCredentials;
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
public class DigestGetterImpl implements DigestGetter {
    private final RegistryCredentials allCredentials;

    public static DigestGetterImpl digestGetter(OsdfPaths paths) {
        RegistryCredentials credentials = settingsFile(RegistryCredentials.class, paths.settings().registryCredentials())
                .getSettings();
        return new DigestGetterImpl(credentials);
    }

    @Override
    public String get(String imageUrl) {
        Image image = new Image(imageUrl);

        String output = execute("curl -k " +
                "-H \"Authorization: Bearer " + token(image.host()) + "\" " +
                "-H \"Accept: application/vnd.docker.distribution.manifest.v2+json\" " +
                image.manifestUrl() + " -D -");
        String digestHeader = stream(output.split("\n"))
                .filter(line -> line.contains("Docker-Content-Digest"))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Digest was not for " + image.host()))
                .strip();
        return digestHeader.split(" ")[1];
    }

    private String token(String host) {
        String output = execute("curl -k -u " + withQuotes(credentialsForUrl(host)) + " https://" + host + "/v2/token");
        Matcher matcher = compile(".*\"(DockerToken.*)\".*").matcher(output);
        if (!matcher.matches()) throw new OSDFException("Unknown registry token format");
        return matcher.group(1);
    }

    private String credentialsForUrl(String host) {
        Credentials credentials = allCredentials.getForUrl(host);
        if (credentials == null) throw new OSDFException("No registry credentials found for " + host);
        return credentials.getCredentialsString();
    }

    private static class Image {
        private final String host;
        private final String path;

        public Image(String imageUrl) {
            String[] hostAndPath = hostAndPath(imageUrl);
            this.host = hostAndPath[0];
            this.path = hostAndPath[1];
        }

        public String host() {
            return host;
        }

        public String path() {
            return path;
        }

        private String[] hostAndPath(String imageUrl) {
            return imageUrl
                    .replaceFirst("http://", "")
                    .replaceFirst("https://", "")
                    .replaceFirst("/", "---")
                    .split("---");
        }

        public String manifestUrl() {
            String[] pathAndTag = path.split(":");
            return "https://" + host + "/v2/" + pathAndTag[0] + "/manifests/" + pathAndTag[1];
        }
    }
}
