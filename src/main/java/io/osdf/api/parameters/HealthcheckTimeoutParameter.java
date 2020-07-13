package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

import static io.osdf.api.lib.parameter.ParameterUtils.toInteger;

public class HealthcheckTimeoutParameter extends ArgParameter<Integer> {
    public HealthcheckTimeoutParameter() {
        super("timeout", "t", "Maximum waiting time for healthy response");
    }

    @Override
    public Integer get() {
        return toInteger(getValue());
    }
}
