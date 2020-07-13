package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

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
