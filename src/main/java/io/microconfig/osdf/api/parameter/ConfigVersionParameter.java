package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class ConfigVersionParameter extends ArgParameter<String> {
    public ConfigVersionParameter() {
        super("version", "v", "Version of config or branch for git");
    }
}
