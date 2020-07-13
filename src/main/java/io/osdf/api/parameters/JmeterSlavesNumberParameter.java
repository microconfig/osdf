package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

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
