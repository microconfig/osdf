package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.common.Credentials;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import static java.util.Map.of;

@Getter
@Setter
@NoArgsConstructor
public class RegistryCredentials {
    private Map<String, Credentials> credentialsMap;

    public void add(String url, Credentials credentials) {
        if (credentialsMap == null) {
            credentialsMap = of(url, credentials);
        } else {
            credentialsMap.put(url, credentials);
        }
    }

    public Credentials getForUrl(String url) {
        return credentialsMap != null ? credentialsMap.get(url) : null;
    }
}
