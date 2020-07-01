package io.osdf.core.connection.cli.kubernetes;

import io.osdf.common.Credentials;
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
