package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class ModeParameter extends AbstractParameter<String> {
    public ModeParameter() {
        super("mode", "m", "Deploy mode: replace or hidden");
    }
}
