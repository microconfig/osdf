package io.osdf.common;

import io.osdf.common.exceptions.OSDFException;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Credentials {
    private String username;
    private String password;
    private String credentialsString;

    public static Credentials of(String credentialsString) {
        String[] split = credentialsString.split(":");
        if (split.length != 2) throw new OSDFException("Bad credentials format " + credentialsString);
        return new Credentials(split[0], split[1], credentialsString);
    }
}
