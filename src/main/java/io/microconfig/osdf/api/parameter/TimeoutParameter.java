package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.utils.StringUtils.castToInteger;

public class TimeoutParameter extends ArgParameter<Integer> {
    public TimeoutParameter() {
        super("timeout", "t", "Stop after T seconds. A timeout of 0 will run command without " +
                "any timeouts (run forever).");
    }

    @Override
    public Integer get() {
        Integer timeout = castToInteger(getValue());
        if (timeout == null) throw new RuntimeException("Invalid integer format " + getValue());
        return timeout;
    }
}
