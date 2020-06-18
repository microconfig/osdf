package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class PodParameter extends ArgParameter<String> {
    public PodParameter() {
        super("pod", "p", "Pod name or number. Pod number - order of pod in <osdf pods> output");
    }
}
