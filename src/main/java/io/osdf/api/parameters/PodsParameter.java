package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

import java.util.List;

import static io.osdf.api.lib.parameter.ParameterUtils.toList;

public class PodsParameter extends ArgParameter<List<String>> {
    public PodsParameter() {
        super("pods", "p", "Comma separated list of pods names or their numbers. Pod number - order of pod in <osdf pods> output");
    }

    @Override
    public List<String> get() {
        return toList(getValue());
    }
}
