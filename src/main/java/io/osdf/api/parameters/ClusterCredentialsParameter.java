package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;
import io.osdf.common.Credentials;

import static io.osdf.common.Credentials.of;

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
