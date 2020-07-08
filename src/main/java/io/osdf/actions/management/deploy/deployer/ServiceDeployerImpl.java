package io.osdf.actions.management.deploy.deployer;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.info;
import static io.microconfig.utils.Logger.warn;
import static io.osdf.actions.management.deploy.cleaner.ResourceDeleter.resourceDeleter;
import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;

@RequiredArgsConstructor
public class ServiceDeployerImpl implements ServiceDeployer {
    private final ClusterCli cli;

    public static ServiceDeployerImpl serviceDeployer(ClusterCli cli) {
        return new ServiceDeployerImpl(cli);
    }

    @Override
    public void deploy(ServiceApplication service) {
        cleanResources(service);

        int currentDeploymentVersion = currentDeploymentVersion(service);
        service.uploadDescription();
        uploadResources(service.files());
        waitForPodsToStart(service, currentDeploymentVersion + 1);
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

    private void waitForPodsToStart(ServiceApplication application, int deploymentVersion) {
        ClusterResource deployment = application.deployment().toResource();
        if (!deployment.kind().equalsIgnoreCase("deploymentconfig")) return;

        String rcName = deployment.name() + "-" + deploymentVersion;
        int rcWaitTime = 10;
        while (rcWaitTime > 0) {
            if (getRcReplicas(rcName) > 0) return;
            rcWaitTime--;
            sleepSec(1);
        }
        warn("Pods of " + application.name() + " haven't started yet");
    }

    private int getRcReplicas(String rcName) {
        CliOutput output = cli.execute("get rc " + rcName + " -o custom-columns=\"replicas:.status.replicas\"");
        List<String> outputLines = output.getOutputLines();
        if (!output.ok() || outputLines.size() != 2) return 0;

        Integer replicas = castToInteger(outputLines.get(1).trim());
        return replicas == null ? 0 : replicas;
    }

    private int currentDeploymentVersion(ServiceApplication application) {
        if (!application.exists()) return 0;

        try {
            Integer version = castToInteger(resourceProperties(cli, application.deployment().toResource(), of(
                    "version", "status.latestVersion"
            )).get("version"));
            return version == null ? 0 : version;
        } catch (OSDFException e) {
            return 0;
        }
    }
}
