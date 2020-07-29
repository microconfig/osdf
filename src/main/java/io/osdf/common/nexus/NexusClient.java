package io.osdf.common.nexus;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.Credentials;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.StringUtils.withQuotes;
import static java.nio.file.Files.deleteIfExists;
import static java.util.List.of;

@RequiredArgsConstructor
public class NexusClient {
    private final String nexusUrl;
    private final Credentials credentials;

    public static NexusClient nexusClient(String nexusUrl, Credentials credentials) {
        return new NexusClient(nexusUrl, credentials);
    }

    public void download(NexusArtifact artifact, Path destination) {
        String url = artifact.getDownloadUrl(nexusUrl);
        executeDownloadCommand(destination, url);
        throwExceptionIfFileIsEmpty(destination);
    }

    private void throwExceptionIfFileIsEmpty(Path destination) {
        if (new File(destination.toString()).length() == 0) {
            try {
                deleteIfExists(destination);
            } catch (IOException ignored) {
                //no need to handle
            }
            throw new OSDFException("Can't download artifact from Nexus. Maybe check credentials.");
        }
    }

    private void executeDownloadCommand(Path destination, String url) {
        String command = "curl -k " + url + " --output " + destination;
        if (credentials == null) {
            execute(command);
        } else {
            execute(command + " -u " + withQuotes(credentials.getCredentialsString()),
                    of(credentials.getCredentialsString()));
        }
    }
}
