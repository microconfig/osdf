package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class PodParameter extends ArgParameter<String> {
    public PodParameter() {
        super("pod", "p", "Pod name or number. Pod number - order of pod in <osdf pods> output");
    }
}
