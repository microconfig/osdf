package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.FlagParameter;

public class SmartDeployParameter extends FlagParameter {
    public SmartDeployParameter() {
        super("smart", "s", "If true, osdf will not redeploy" +
                "unchanged services or rerun unchanged jobs");
    }
}
