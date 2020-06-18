package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class OpenShiftPasswordParameter extends ArgParameter<String> {
    public OpenShiftPasswordParameter() {
        super("password", "p", "OpenShift password");
    }
}
