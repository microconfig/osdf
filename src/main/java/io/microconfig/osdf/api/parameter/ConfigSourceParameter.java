package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;
import io.microconfig.osdf.state.ConfigSource;

import static io.microconfig.osdf.state.ConfigSource.*;


public class ConfigSourceParameter extends AbstractParameter<ConfigSource> {
    public ConfigSourceParameter() {
        super("configSource", "s", "Config source: git, nexus or local");
    }

    @Override
    public ConfigSource get() {
        if (getValue() == null) return null;
        return valueOf(getValue().toUpperCase());
    }
}
