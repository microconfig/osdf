package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class EnvParameter extends ArgParameter<String> {
    public EnvParameter() {
        super("env", "e", "Environment");
    }
}
