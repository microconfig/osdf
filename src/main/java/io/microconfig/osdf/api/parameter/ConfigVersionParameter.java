package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

public class ConfigVersionParameter extends AbstractParameter<String> {
    public ConfigVersionParameter() {
        super("configVersion", "v", "Version of config or branch for git");
    }
}
