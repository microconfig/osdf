package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;
import io.osdf.settings.version.OsdfVersion;

import static io.osdf.settings.version.OsdfVersion.fromString;

public class OSDFVersionParameter extends ArgParameter<OsdfVersion> {
    public OSDFVersionParameter() {
        super("version", "v", "Target OSDF version");
    }

    @Override
    public OsdfVersion get() {
        if (getValue() == null) return null;
        return fromString(getValue());
    }
}
