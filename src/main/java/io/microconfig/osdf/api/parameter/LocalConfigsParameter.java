package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import java.nio.file.Path;

import static java.nio.file.Path.of;

public class LocalConfigsParameter extends ArgParameter<Path> {
    public LocalConfigsParameter() {
        super("path", "p", "Local path to configs");
    }

    @Override
    public Path get() {
        return getValue() != null ? of(getValue()) : null;
    }
}
