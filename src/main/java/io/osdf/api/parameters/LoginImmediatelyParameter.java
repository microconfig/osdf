package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.FlagParameter;

public class LoginImmediatelyParameter extends FlagParameter {
    public LoginImmediatelyParameter() {
        super("login", "l", "If true, try to login to cluster");
    }
}
