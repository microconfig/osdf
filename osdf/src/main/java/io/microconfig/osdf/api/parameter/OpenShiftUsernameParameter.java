package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class OpenShiftUsernameParameter extends ArgParameter<String> {
    public OpenShiftUsernameParameter() {
        super("username", "u", "OpenShift username");
    }
}
