package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class CommandParameter extends AbstractParameter<String> {
    public CommandParameter() {
        super("command", "cmd", "OSDF command");
    }
}
