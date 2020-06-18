package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.common.Credentials.of;

public class RegistryCredentialsParameter extends ArgParameter<Credentials> {
    public RegistryCredentialsParameter() {
        super("credentials", "c", "Credentials for registry. Format: user:password");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
