package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;
import io.osdf.common.Credentials;

import static io.osdf.common.Credentials.*;

public class NexusCredentialsParameter extends ArgParameter<Credentials> {
    public NexusCredentialsParameter() {
        super("credentials", "c", "Credentials for Nexus: login:password");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
