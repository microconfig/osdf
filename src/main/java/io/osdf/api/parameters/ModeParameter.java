package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class ModeParameter extends ArgParameter<String> {
    public ModeParameter() {
        super("mode", "m", "Deploy mode: replace or hidden");
    }
}
