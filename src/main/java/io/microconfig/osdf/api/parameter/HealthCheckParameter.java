package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.FlagParameter;

public class HealthCheckParameter extends FlagParameter {
    public HealthCheckParameter() {
        super("healthcheck", "h", "Check pods health. This option slows down status command");
    }
}
