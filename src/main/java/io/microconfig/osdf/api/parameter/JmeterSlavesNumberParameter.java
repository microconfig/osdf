package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import static java.lang.Integer.valueOf;

public class JmeterSlavesNumberParameter extends ArgParameter<Integer> {
    public JmeterSlavesNumberParameter() {
        super("slavesNumber", "n", "Number of slaves nodes");
    }

    @Override
    public Integer get() {
        return getValue() != null ? valueOf(getValue()) : null;
    }
}
