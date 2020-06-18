package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class ComponentParameter extends ArgParameter<String> {
    public ComponentParameter() {
        super("component", "c", "Service component");
    }
}
