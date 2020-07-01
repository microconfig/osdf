package io.osdf.api.parameters;

import io.osdf.common.Credentials;
import io.osdf.api.lib.parameter.ArgParameter;

import static io.osdf.common.Credentials.of;

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
