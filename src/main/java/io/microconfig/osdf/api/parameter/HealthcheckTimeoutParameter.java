package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.parameters.ParameterUtils.toInteger;

public class HealthcheckTimeoutParameter extends ArgParameter<Integer> {
    public HealthcheckTimeoutParameter() {
        super("timeout", "t", "Maximum waiting time for healthy response");
    }

    @Override
    public Integer get() {
        return toInteger(getValue());
    }
}
