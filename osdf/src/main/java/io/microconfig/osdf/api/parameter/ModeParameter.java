package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class ModeParameter extends ArgParameter<String> {
    public ModeParameter() {
        super("mode", "m", "Deploy mode: replace or hidden");
    }
}
