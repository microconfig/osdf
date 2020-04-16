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
        String[] split = credentialsString.split(":");
        if (split.length == 2) return new OpenShiftCredentials(new Credentials(split[0], split[1], credentialsString));
        if (split.length == 1) return new OpenShiftCredentials(split[0]);
        throw new RuntimeException("The format of \"osdf init -oc\" command isn't correct. " +
                                    "Please input \"login:password\" or \"token\"");
    }

    public String getLoginParams() {
        if (isEmpty(token)) {
            return " -u " + credentials.getUsername() + " -p " + credentials.getPassword();
        } else {
            return " --token=" + token;
        }
    }
}
