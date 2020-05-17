package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class EnvParameter extends ArgParameter<String> {
    public EnvParameter() {
        super("env", "e", "Environment");
    }
}
