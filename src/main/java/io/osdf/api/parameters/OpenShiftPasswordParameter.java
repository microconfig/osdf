package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class OpenShiftPasswordParameter extends ArgParameter<String> {
    public OpenShiftPasswordParameter() {
        super("password", "p", "OpenShift password");
    }
}
