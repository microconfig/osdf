package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.common.Credentials;

import static io.microconfig.osdf.common.Credentials.*;

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
