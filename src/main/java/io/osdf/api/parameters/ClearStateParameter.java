package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.FlagParameter;

public class ClearStateParameter extends FlagParameter {
    public ClearStateParameter() {
        super("clear-state", "c", "Clear state before install");
    }
}
