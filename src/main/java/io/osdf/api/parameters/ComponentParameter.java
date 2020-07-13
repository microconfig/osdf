package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class ComponentParameter extends ArgParameter<String> {
    public ComponentParameter() {
        super("component", "c", "Service component");
    }
}
