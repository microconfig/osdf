package io.microconfig.osdf.nexus;

import io.microconfig.osdf.state.Credentials;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;

@RequiredArgsConstructor
public class NexusClient {
    private final String nexusUrl;
    private final Credentials credentials;

    public static NexusClient nexusClient(String nexusUrl, Credentials credentials) {
        return new NexusClient(nexusUrl, credentials);
    }

    public void download(NexusArtifact artifact, Path destination) {
        String url = artifact.getDownloadUrl(nexusUrl);

        String command = "curl " + url + " --output " + destination;
        if (credentials != null) command += " -n " + credentials.getCredentialsString();
        execute(command);
    }
}
