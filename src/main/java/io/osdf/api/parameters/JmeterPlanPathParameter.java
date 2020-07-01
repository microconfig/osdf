package io.osdf.api.parameters;

import io.osdf.api.lib.parameter.ArgParameter;

import java.nio.file.Path;

import static java.nio.file.Path.of;

public class JmeterPlanPathParameter extends ArgParameter<Path> {
    public JmeterPlanPathParameter() {
        super("jmeterPlanPath", "file", "Absolute path to jmeter plan file .jmx");
    }

    @Override
    public Path get() {
        return getValue() != null ? of(getValue()) : null;
    }
}
