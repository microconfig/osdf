package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class GitUrlParameter extends ArgParameter<String> {
    public GitUrlParameter() {
        super("url", "u", "Git clone url");
    }
}
