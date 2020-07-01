package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class OpenShiftUsernameParameter extends ArgParameter<String> {
    public OpenShiftUsernameParameter() {
        super("username", "u", "OpenShift username");
    }
}
