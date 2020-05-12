package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class JmeterSlavesNumberParameter extends ArgParameter<Integer> {
    public JmeterSlavesNumberParameter() {
        super("slavesNumber", "n", "Number of slaves nodes");
    }

    @Override
    public Integer get() {
        return getValue() != null ? Integer.valueOf(getValue()) : null;
    }
}
