package io.osdf.actions.init.configs.postprocess.types;

import io.osdf.core.service.metadata.LocalResourceMetadata;

import java.util.function.Predicate;

public enum ComponentType {
    SERVICE(metadata -> metadata.getKind().equals("deployment") || metadata.getKind().equals("deploymentconfig")),
    JOB(metadata -> metadata.getKind().equals("job"));

    private final Predicate<LocalResourceMetadata> condition;

    ComponentType(Predicate<LocalResourceMetadata> condition) {
        this.condition = condition;
    }

    public Predicate<LocalResourceMetadata> condition() {
        return condition;
    }
}
