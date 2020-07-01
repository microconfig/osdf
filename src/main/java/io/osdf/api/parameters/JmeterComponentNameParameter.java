package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class JmeterComponentNameParameter extends ArgParameter<String> {
    public JmeterComponentNameParameter() {
        super("componentName", "component", "The name of load test component.");
    }

    @Override
    public String get() {
        return getValue();
    }
}
