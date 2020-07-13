package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.FlagParameter;

public class HealthCheckParameter extends FlagParameter {
    public HealthCheckParameter() {
        super("healthcheck", "h", "Check pods health. This option slows down status command");
    }
}
