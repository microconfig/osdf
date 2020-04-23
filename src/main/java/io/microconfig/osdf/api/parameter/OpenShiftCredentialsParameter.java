package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.state.OpenShiftCredentials;

import static io.microconfig.osdf.state.OpenShiftCredentials.of;

public class OpenShiftCredentialsParameter extends ArgParameter<OpenShiftCredentials> {
    public OpenShiftCredentialsParameter() {
        super("openshiftCredentials", "oc", "Credentials for OpenShift: login:password or token");
    }

    @Override
    public OpenShiftCredentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
