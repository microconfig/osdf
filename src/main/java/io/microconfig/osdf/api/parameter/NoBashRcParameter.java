package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.FlagParameter;

public class NoBashRcParameter extends FlagParameter {
    public NoBashRcParameter() {
        super("nobashrc", "nb", "Do not add .bashrc entry");
    }
}
