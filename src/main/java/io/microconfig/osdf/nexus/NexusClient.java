package io.microconfig.osdf.nexus;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.common.Credentials;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.StringUtils.withQuotes;
import static java.nio.file.Files.deleteIfExists;

@RequiredArgsConstructor
public class NexusClient {
    private final String nexusUrl;
    private final Credentials credentials;

    public static NexusClient nexusClient(String nexusUrl, Credentials credentials) {
        return new NexusClient(nexusUrl, credentials);
    }

    public void download(NexusArtifact artifact, Path destination) {
        String url = artifact.getDownloadUrl(nexusUrl);
        execute(withCredentials("curl -k " + url + " --output " + destination));
        throwExceptionIfFileIsEmpty(destination);
    }

    private void throwExceptionIfFileIsEmpty(Path destination) {
        if (new File(destination.toString()).length() == 0) {
            try {
                deleteIfExists(destination);
            } catch (IOException ignored) {
            }
            throw new OSDFException("Can't download artifact from Nexus. Maybe check credentials.");
        }
    }

    private String withCredentials(String command) {
        if (credentials == null) return command;
        return command + " -u " + withQuotes(credentials.getCredentialsString());
    }
}
