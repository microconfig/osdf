package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class OpenShiftTokenParameter extends ArgParameter<String> {
    public OpenShiftTokenParameter() {
        super("token", "t", "Token for OpenShift");
    }
}
