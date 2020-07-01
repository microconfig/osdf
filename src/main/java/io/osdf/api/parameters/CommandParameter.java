package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

import java.util.List;

import static io.osdf.api.lib.parameter.ParameterUtils.toList;

public class CommandParameter extends ArgParameter<List<String>> {
    public CommandParameter() {
        super("command", "c", "OSDF command");
    }

    @Override
    public List<String> get() {
        return toList(getValue());
    }
}
