package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class JmeterTestNameParametr extends ArgParameter<String> {
    public JmeterTestNameParametr() {
        super("testConfigName", "config", "The name of test config file.");
    }

    @Override
    public String get() {
        return getValue();
    }
}
