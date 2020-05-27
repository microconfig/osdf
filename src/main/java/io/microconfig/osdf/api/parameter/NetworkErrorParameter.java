package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.parameters.ArgParameter;

import static io.microconfig.osdf.utils.StringUtils.castToInteger;

public class NetworkErrorParameter extends ArgParameter<Integer> {
    public NetworkErrorParameter() {
        super("errorCode", "e", "HTTP error which will be injected in Istio Virtual Service");
    }

    @Override
    public Integer get() {
        if (getValue() == null) return null;
        Integer delay = castToInteger(getValue());
        if (delay == null) throw new OSDFException("Invalid integer format " + getValue());
        return delay;
    }
}
