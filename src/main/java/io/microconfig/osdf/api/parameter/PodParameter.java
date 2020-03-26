package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class PodParameter extends AbstractParameter<String> {
    public PodParameter() {
        super("pod", "p", "Pod name or number. Pod number - order of pod in <osdf pods> output");
    }
}
