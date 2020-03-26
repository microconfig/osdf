package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.AbstractParameter;

import java.nio.file.Path;

import static java.nio.file.Path.of;

public class LocalConfigsParameter extends AbstractParameter<Path> {
    public LocalConfigsParameter() {
        super("local", "local", "Local path to configs");
    }

    @Override
    public Path get() {
        return getValue() != null ? of(getValue()) : null;
    }
}
