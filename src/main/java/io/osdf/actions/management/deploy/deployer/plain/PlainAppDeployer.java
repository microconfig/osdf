package io.osdf.actions.management.deploy.deployer.plain;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.osdf.actions.management.deploy.deployer.ImmutableAwareUploader.immutableAwareUploader;
import static io.osdf.actions.management.deploy.deployer.ResourceDeleter.resourceDeleter;

@RequiredArgsConstructor
public class PlainAppDeployer {
    private final ClusterCli cli;

    public static PlainAppDeployer plainAppDeployer(ClusterCli cli) {
        return new PlainAppDeployer(cli);
    }

    public boolean deploy(PlainApplication plainApp) {
        try {
            cleanResources(plainApp);
            plainApp.uploadDescription();
            immutableAwareUploader(cli).uploadResources(plainApp.files());
        } catch (OSDFException e) {
            error(e.getMessage());
            return false;
        }
        return true;
    }

    private void cleanResources(PlainApplication plainApp) {
        plainApp.coreDescription().ifPresent(coreDescription ->
                resourceDeleter(cli)
                        .deleteOldResources(coreDescription, plainApp.files())
                        .deleteConfigMaps(coreDescription));
    }
}
