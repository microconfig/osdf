package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class EnvParameter extends AbstractParameter<String> {
    public EnvParameter() {
        super("env", "e", "Environment");
    }
}
