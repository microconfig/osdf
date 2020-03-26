package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class GitUrlParameter extends AbstractParameter<String> {
    public GitUrlParameter() {
        super("gitUrl", "g", "Git clone url");
    }
}
