package io.microconfig.osdf.state;

import lombok.*;

import static io.microconfig.utils.StringUtils.isEmpty;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class OpenShiftCredentials {
    private String token;
    private String credentialsString;
    private Credentials credentials;

    public OpenShiftCredentials(String token) {
        this.token = token;
    }

    public OpenShiftCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public static OpenShiftCredentials of(String credentialsString) {
        return !isToken(credentialsString) ?
                new OpenShiftCredentials(Credentials.of(credentialsString)) :
                new OpenShiftCredentials(credentialsString);
    }

    private static boolean isToken(String credentialsString) {
        String[] split = credentialsString.split(":");
        return split.length == 1;
    }

    public String getLoginParams() {
        if (isEmpty(token)) {
            return " -u " + credentials.getUsername() + " -p " + credentials.getPassword();
        } else {
            return " --token=" + token;
        }
    }
}
