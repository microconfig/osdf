package io.osdf.core.connection.cli.openshift;

import io.osdf.common.Credentials;
import lombok.*;

import static io.osdf.common.utils.StringUtils.withQuotes;
import static io.microconfig.utils.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class OpenShiftCredentials {
    private String token;
    private Credentials credentials;

    public OpenShiftCredentials(String token) {
        this.token = token;
    }

    public OpenShiftCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public static OpenShiftCredentials of(String credentialsString) {
        return isToken(credentialsString) ? new OpenShiftCredentials(credentialsString)
                : new OpenShiftCredentials(Credentials.of(credentialsString));
    }

    private static boolean isToken(String credentialsString) {
        String[] split = credentialsString.split(":");
        return split.length == 1;
    }

    public String getLoginParams() {
        if (isEmpty(token)) {
            return " -u " + withQuotes(credentials.getUsername()) + " -p " + withQuotes(credentials.getPassword());
        } else {
            return " --token=" + token;
        }
    }

    @Override
    public String toString() {
        if (token == null && credentials == null) return "Not configured";
        if (credentials != null) return "User: " + credentials.getUsername();
        return "Token: ***";
    }
}
