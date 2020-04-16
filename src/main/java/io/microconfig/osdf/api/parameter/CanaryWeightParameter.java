package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.utils.StringUtils.castToInteger;

public class CanaryWeightParameter extends ArgParameter<Integer> {
    public CanaryWeightParameter() {
        super("weight", "w", "Traffic weight for canary release. Integer from 0 to 100");
    }

    @Override
    public Integer get() {
        if (getValue() == null) return null;
        Integer weight = castToInteger(getValue());
        if (weight == null) throw new RuntimeException("Invalid integer format " + getValue());
        return weight;
    }
}
