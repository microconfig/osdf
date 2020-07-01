package io.osdf.core.local.configs.update.fetch.nexus;

import io.osdf.common.nexus.NexusArtifact;
import io.osdf.common.Credentials;
import lombok.Getter;
import lombok.Setter;

import static io.microconfig.utils.Logger.error;

@Getter
@Setter
public class NexusFetcherSettings {
    private String url;
    private NexusArtifact artifact;
    private Credentials credentials;

    public boolean verifyAndLogErrors() {
        if (url == null) {
            error("Nexus url is not specified");
            return false;
        }
        if (artifact == null) {
            error("Nexus configs artifact is not specified");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" +
                (url == null ? "" :
                        "Url: " + url + "\n") +
                (artifact == null ? "" :
                        "Artifact: " + artifact + "\n") +
                (credentials == null ? "" :
                        "Username: " + credentials.getUsername());
    }
}
