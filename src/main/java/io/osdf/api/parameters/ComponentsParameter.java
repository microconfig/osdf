package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

import java.util.List;

import static io.osdf.api.lib.parameter.ParameterUtils.toList;

public class ComponentsParameter extends ArgParameter<List<String>> {
    public ComponentsParameter() {
        super("components", "c", "Comma separated list of components");
    }

    @Override
    public List<String> get() {
        return toList(getValue());
    }
}
