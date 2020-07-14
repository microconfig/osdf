package io.osdf.actions.management.deploy.deployer.service;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.management.deploy.deployer.ResourceDeleter.resourceDeleter;

@RequiredArgsConstructor
public class ServiceDeployer {
    private final ClusterCli cli;

    public static ServiceDeployer serviceDeployer(ClusterCli cli) {
        return new ServiceDeployer(cli);
    }

    public boolean deploy(ServiceApplication service) {
        try {
            cleanResources(service);
            service.uploadDescription();
            uploadResources(service.files());
        } catch (OSDFException e) {
            error(e.getMessage());
            return false;
        }
        return true;
    }

    private void cleanResources(ServiceApplication application) {
        if (application.exists()) {
            resourceDeleter(cli)
                    .deleteOldResources(application.coreDescription(), application.files())
                    .deleteConfigMaps(application.coreDescription());
        }
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
