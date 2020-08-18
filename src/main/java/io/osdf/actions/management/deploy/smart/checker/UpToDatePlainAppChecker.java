package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;

import java.util.Optional;

public class UpToDatePlainAppChecker implements UpToDateChecker {
    public static UpToDatePlainAppChecker upToDatePlainAppChecker() {
        return new UpToDatePlainAppChecker();
    }

    @Override
    public boolean check(Application app) {
        Optional<CoreDescription> description = app.coreDescription();
        if (description.isEmpty()) return false;

        YamlObject deployProperties = app.files().deployProperties();
        return description.get().getConfigVersion().equals(deployProperties.get("config.version"));
    }
}
