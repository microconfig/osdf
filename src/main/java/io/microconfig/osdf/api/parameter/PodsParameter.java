package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import java.util.List;

import static io.microconfig.osdf.parameters.ParameterUtils.toList;

public class PodsParameter extends ArgParameter<List<String>> {
    public PodsParameter() {
        super("pods", "p", "Comma separated list of pods names or their numbers. Pod number - order of pod in <osdf pods> output");
    }

    @Override
    public List<String> get() {
        return toList(getValue());
    }
}
