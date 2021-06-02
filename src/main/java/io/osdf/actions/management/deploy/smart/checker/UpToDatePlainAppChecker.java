package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.plain.PlainAppDescription;
import io.osdf.core.application.plain.PlainApplication;

import java.util.Optional;

import static io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.core.application.plain.PlainApplication.plainApplication;

public class UpToDatePlainAppChecker implements UpToDateChecker {
    public static UpToDatePlainAppChecker upToDatePlainAppChecker() {
        return new UpToDatePlainAppChecker();
    }

    @Override
    public boolean check(Application app) {
        PlainApplication plainApp = plainApplication(app);
        Optional<PlainAppDescription> description = plainApp.loadDescription(PlainAppDescription.class, "plain");
        if (description.isEmpty()) return false;

        String remoteHash = description.get().getConfigHash();
        if (remoteHash == null) return false;

        String localHash = resourcesHashComputer().currentHash(app.files());
        return remoteHash.equals(localHash);
    }
}
