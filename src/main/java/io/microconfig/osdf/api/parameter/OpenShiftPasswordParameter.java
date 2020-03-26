package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class OpenShiftPasswordParameter extends AbstractParameter<String> {
    public OpenShiftPasswordParameter() {
        super("password", "p", "OpenShift password");
    }
}
