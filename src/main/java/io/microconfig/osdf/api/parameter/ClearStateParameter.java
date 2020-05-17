package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.FlagParameter;

public class ClearStateParameter extends FlagParameter {
    public ClearStateParameter() {
        super("clear-state", "c", "Clear state before install");
    }
}
