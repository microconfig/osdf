package io.osdf.actions.management.deploy.deployer.service;

import io.osdf.actions.info.status.service.ServiceStatus;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.microconfig.utils.Logger.info;
import static io.osdf.actions.info.status.service.ServiceStatus.FAILED;
import static io.osdf.actions.info.status.service.ServiceStatus.NOT_READY;
import static io.osdf.actions.info.status.service.ServiceStatusGetter.serviceStatusGetter;
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
        application.coreDescription().ifPresent(coreDescription -> {
            resourceDeleter(cli)
                    .deleteOldResources(coreDescription, application.files())
                    .deleteConfigMaps(coreDescription);
            deleteDeploymentIfNotReadyOrRunning(application);
        });
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

    private void deleteDeploymentIfNotReadyOrRunning(ServiceApplication application) {
        ServiceStatus status = serviceStatusGetter(cli).statusOf(application);
        if (status == NOT_READY || status == FAILED) {
            info("Deployment " + application.name() + " is " + status + " and will be deleted before deploy");
            application.deployment().ifPresent(deployment -> deployment.toResource().delete(cli));
        }
    }
}
