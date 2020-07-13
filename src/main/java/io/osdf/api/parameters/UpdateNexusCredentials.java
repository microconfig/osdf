package io.osdf.api.parameters;

import io.osdf.common.Credentials;
import io.osdf.api.lib.parameter.ArgParameter;

import static io.osdf.common.Credentials.of;

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
