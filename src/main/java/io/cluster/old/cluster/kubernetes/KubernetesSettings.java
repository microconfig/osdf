package io.cluster.old.cluster.kubernetes;

import io.microconfig.osdf.common.Credentials;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KubernetesSettings {
    private Credentials credentials;

    @Override
    public String toString() {
        return credentials == null ? "Not configured" : "User: " + credentials.getUsername();
    }
}
