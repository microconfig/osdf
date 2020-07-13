package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class GitUrlParameter extends ArgParameter<String> {
    public GitUrlParameter() {
        super("url", "u", "Git clone url");
    }
}
