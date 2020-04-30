package io.microconfig.osdf.api;

import io.microconfig.osdf.common.Credentials;
import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.common.Credentials.of;

public class UpdateNexusCredentials extends ArgParameter<Credentials> {
    public UpdateNexusCredentials() {
        super("credentials", "c", "Credentials for nexus with OSDF");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
