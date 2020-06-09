package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class JmeterComponentNameParameter extends ArgParameter<String> {
    public JmeterComponentNameParameter() {
        super("componentName", "component", "The name of load test component.");
    }

    @Override
    public String get() {
        return getValue();
    }
}
