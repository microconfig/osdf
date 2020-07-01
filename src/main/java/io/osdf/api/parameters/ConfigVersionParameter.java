package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

public class ConfigVersionParameter extends ArgParameter<String> {
    public ConfigVersionParameter() {
        super("version", "v", "Version of config or branch for git");
    }
}
