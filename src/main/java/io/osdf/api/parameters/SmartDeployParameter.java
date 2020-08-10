package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.FlagParameter;

public class SmartDeployParameter extends FlagParameter {
    public SmartDeployParameter() {
        super("smart", "s", "If true, osdf will not redeploy unchanged services or rerun unchanged jobs");
    }
}
