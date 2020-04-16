package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.FlagParameter;

public class WaitParameter extends FlagParameter {
    public WaitParameter() {
        super("wait", "w", "Wait for deployments to run");
    }
}
