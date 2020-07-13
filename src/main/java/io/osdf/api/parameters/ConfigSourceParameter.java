package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;
import io.osdf.core.local.configs.ConfigsSource;

import static io.osdf.core.local.configs.ConfigsSource.*;


public class ConfigSourceParameter extends ArgParameter<ConfigsSource> {
    public ConfigSourceParameter() {
        super("configSource", "s", "Config source: git, nexus or local");
    }

    @Override
    public ConfigsSource get() {
        if (getValue() == null) return null;
        return valueOf(getValue().toUpperCase());
    }
}
