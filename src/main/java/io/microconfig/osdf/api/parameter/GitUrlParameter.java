package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class GitUrlParameter extends ArgParameter<String> {
    public GitUrlParameter() {
        super("gitUrl", "g", "Git clone url");
    }
}
