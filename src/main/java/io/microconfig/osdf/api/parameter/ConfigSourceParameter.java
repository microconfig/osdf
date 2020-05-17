package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.configs.ConfigsSource;

import static io.microconfig.osdf.configs.ConfigsSource.*;


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
