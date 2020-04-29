package io.microconfig.osdf.configfetcher.nexus;

import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.Credentials;
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
}
