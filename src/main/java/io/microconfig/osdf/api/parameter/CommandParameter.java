package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class CommandParameter extends ArgParameter<String> {
    public CommandParameter() {
        super("command", "cmd", "OSDF command");
    }
}
