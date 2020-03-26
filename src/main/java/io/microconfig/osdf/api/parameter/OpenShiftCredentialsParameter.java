package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;
import io.microconfig.osdf.state.Credentials;

import static io.microconfig.osdf.state.Credentials.of;

public class OpenShiftCredentialsParameter extends AbstractParameter<Credentials> {
    public OpenShiftCredentialsParameter() {
        super("openshiftCredentials", "oc", "Credentials for OpenShift: login:password");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        Credentials credentials = of(getValue());
        if (credentials == null) throw new RuntimeException("Bad format for credentials");
        return credentials;
    }
}
