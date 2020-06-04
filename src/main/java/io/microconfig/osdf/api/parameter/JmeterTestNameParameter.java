package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class JmeterTestNameParameter extends ArgParameter<String> {
    public JmeterTestNameParameter() {
        super("testConfigName", "config", "The name of test config file.");
    }

    @Override
    public String get() {
        return getValue();
    }
}
