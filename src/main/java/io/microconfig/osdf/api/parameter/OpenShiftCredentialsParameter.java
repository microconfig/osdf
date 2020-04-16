package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;
import io.microconfig.osdf.state.OpenShiftCredentials;

import static io.microconfig.osdf.state.OpenShiftCredentials.of;

public class OpenShiftCredentialsParameter extends AbstractParameter<OpenShiftCredentials> {
    public OpenShiftCredentialsParameter() {
        super("openshiftCredentials", "oc", "Credentials for OpenShift: login:password or token");
    }

    @Override
    public OpenShiftCredentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}
