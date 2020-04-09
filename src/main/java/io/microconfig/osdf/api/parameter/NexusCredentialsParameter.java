package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;
import io.microconfig.osdf.state.Credentials;

import static io.microconfig.osdf.state.Credentials.*;

public class NexusCredentialsParameter extends AbstractParameter<Credentials> {
    public NexusCredentialsParameter() {
        super("nexusCredentials", "nc", "Credentials for Nexus: login:password");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
