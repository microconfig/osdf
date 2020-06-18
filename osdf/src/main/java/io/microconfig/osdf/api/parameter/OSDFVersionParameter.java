package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.state.OSDFVersion;

import static io.microconfig.osdf.state.OSDFVersion.fromString;

public class OSDFVersionParameter extends ArgParameter<OSDFVersion> {
    public OSDFVersionParameter() {
        super("version", "v", "Target OSDF version");
    }

    @Override
    public OSDFVersion get() {
        if (getValue() == null) return null;
        return fromString(getValue());
    }
}
