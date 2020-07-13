package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

import static io.osdf.api.lib.parameter.ParameterUtils.toInteger;

public class CanaryWeightParameter extends ArgParameter<Integer> {
    public CanaryWeightParameter() {
        super("weight", "w", "Traffic weight for canary release. Integer from 0 to 100");
    }

    @Override
    public Integer get() {
        return toInteger(getValue());
    }
}
