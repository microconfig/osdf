package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class ComponentParameter extends AbstractParameter<String> {
    public ComponentParameter() {
        super("component", "c", "Service component");
    }
}
