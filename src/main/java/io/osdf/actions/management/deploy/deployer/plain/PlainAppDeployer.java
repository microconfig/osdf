package io.osdf.actions.management.deploy.deployer.plain;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.microconfig.utils.Logger.info;
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
            uploadResources(plainApp.files());
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

    private void uploadResources(ApplicationFiles files) {
        CliOutput output = cli.execute("apply -f " + files.getPath("resources"));
        if (!output.ok()) {
            if (output.getOutput().contains("field is immutable")) {
                info("One of resources changed immutable field");
                files.resources().forEach(resource -> resource.upload(cli));
            } else {
                throw new OSDFException("Error deploying " + files.name() + ":" + output.getOutput());
            }
        }
    }
}
