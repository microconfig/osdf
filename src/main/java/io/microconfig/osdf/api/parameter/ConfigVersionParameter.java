package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

public class ConfigVersionParameter extends ArgParameter<String> {
    public ConfigVersionParameter() {
        super("configVersion", "v", "Version of config or branch for git");
    }
}
