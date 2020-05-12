package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class LatestImageVersionGetter {
    private final Credentials credentials;
    private final String host;
    private final String imagePath;

    public static LatestImageVersionGetter latestImageVersionGetter(Credentials credentials, String host, String imagePath) {
        return new LatestImageVersionGetter(credentials, host, imagePath);
    }

    public String get() throws SSLHandshakeException {
        return imageId(token());
    }

    private String token() {
        String output = execute("curl -u " + credentials.getCredentialsString() + " https://" + host + "/v2/token");
        Matcher matcher = compile(".*\"(DockerToken.*)\".*").matcher(output);
        if (!matcher.matches()) throw new OSDFException("Unknown registry token format");
        return matcher.group(1);
    }

    private String imageId(String token) throws SSLHandshakeException {
        String url = manifestUrl();
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setRequestProperty("Accept", "application/vnd.docker.distribution.manifest.v2+json");
            if (con.getResponseCode() != 200) {
                throw new OSDFException(url + " returned " + con.getResponseCode() + " status code");
            }
            return con.getHeaderField("Docker-Content-Digest");
        } catch (SSLHandshakeException e) {
            throw e;
        } catch (IOException e) {
            throw new OSDFException("Error querying " + url, e);
        }
    }

    private String manifestUrl() {
        String[] pathAndTag = imagePath.split(":");
        return "https://" + host + "/v2/" + pathAndTag[0] + "/manifests/" + pathAndTag[1];
    }
}
