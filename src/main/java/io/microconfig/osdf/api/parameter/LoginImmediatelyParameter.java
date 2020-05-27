package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.FlagParameter;

public class LoginImmediatelyParameter extends FlagParameter {
    public LoginImmediatelyParameter() {
        super("login", "l", "If true, try to login to cluster");
    }
}
