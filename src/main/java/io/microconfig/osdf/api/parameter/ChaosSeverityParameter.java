package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.utils.StringUtils.castToInteger;

public class ChaosSeverityParameter extends ArgParameter<Integer> {
    public ChaosSeverityParameter() {
        super("severity", "s", "abstract severity parameter [0:100]");
    }

    @Override
    public Integer get() {
        Integer severity = castToInteger(getValue());
        if (severity == null) throw new RuntimeException("Invalid integer format: " + getValue());
        if ((severity < 0) || (severity > 100)) throw new RuntimeException("Invalid severity value: " + getValue()
                + ". Acceptable range is [0:100]");
        return severity;
    }
}
