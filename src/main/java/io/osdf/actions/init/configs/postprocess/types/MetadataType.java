package io.osdf.actions.init.configs.postprocess.types;

import io.osdf.core.application.core.files.metadata.LocalResourceMetadata;

import java.util.function.Predicate;

public enum MetadataType {
    SERVICE(metadata -> metadata.getKind().equals("deployment") || metadata.getKind().equals("deploymentconfig")),
    JOB(metadata -> metadata.getKind().equals("job")),
    PLAIN(metadata -> false);

    private final Predicate<LocalResourceMetadata> condition;

    MetadataType(Predicate<LocalResourceMetadata> condition) {
        this.condition = condition;
    }

    public Predicate<LocalResourceMetadata> condition() {
        return condition;
    }
}
