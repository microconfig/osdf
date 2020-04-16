package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.state.Credentials;

import static io.microconfig.osdf.state.Credentials.of;

public class OpenShiftCredentialsParameter extends ArgParameter<Credentials> {
    public OpenShiftCredentialsParameter() {
        super("openshiftCredentials", "oc", "Credentials for OpenShift: login:password");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
