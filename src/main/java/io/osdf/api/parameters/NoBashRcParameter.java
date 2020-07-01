package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.FlagParameter;

public class NoBashRcParameter extends FlagParameter {
    public NoBashRcParameter() {
        super("nobashrc", "nb", "Do not add .bashrc entry");
    }
}
