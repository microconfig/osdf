package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import java.nio.file.Path;

import static java.nio.file.Path.of;

public class ChaosExperimentPlanPathParameter extends ArgParameter<Path> {
    public ChaosExperimentPlanPathParameter() {
        super("path", "p", "Path to chaos experiment plan");
    }

    @Override
    public Path get() {
        return getValue() != null ? of(getValue()) : null;
    }
}
