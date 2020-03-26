package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class OpenShiftUsernameParameter extends AbstractParameter<String> {
    public OpenShiftUsernameParameter() {
        super("username", "u", "OpenShift username");
    }
}
