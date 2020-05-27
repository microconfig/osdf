package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.common.Credentials;

import static io.microconfig.osdf.common.Credentials.of;

public class ClusterCredentialsParameter extends ArgParameter<Credentials> {
    public ClusterCredentialsParameter() {
        super("credentials", "c", "Credentials for cluster: login:password or token");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
