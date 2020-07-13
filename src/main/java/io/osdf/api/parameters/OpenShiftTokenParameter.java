package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class OpenShiftTokenParameter extends ArgParameter<String> {
    public OpenShiftTokenParameter() {
        super("token", "t", "Token for OpenShift");
    }
}
