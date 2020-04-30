package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import java.util.List;

import static io.microconfig.osdf.parameters.ParameterUtils.toList;

public class CommandParameter extends ArgParameter<List<String>> {
    public CommandParameter() {
        super("command", "c", "OSDF command");
    }

    @Override
    public List<String> get() {
        return toList(getValue());
    }
}
