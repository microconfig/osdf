package io.osdf.actions.management.restart;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;

import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;

@RequiredArgsConstructor
public class DeploymentRestarter {
    private final ClusterCli cli;

    public static DeploymentRestarter deploymentRestarter(ClusterCli cli) {
        return new DeploymentRestarter(cli);
    }

    public void restart(ServiceApplication application) {
        ClusterDeployment deployment = application.getDeploymentOrThrow();
        Optional<ResourceProperties> properties = resourceProperties(cli, deployment.toResource(), of(
                "replicas", "spec.replicas"
        ));
        if (properties.isEmpty()) throw new OSDFException("Can't get number of replicas for " + deployment.name());

        Integer replicas = castToInteger(properties.get().get("replicas"));
        if (replicas == null || replicas == 0) {
            scaleFromConfigs(deployment, application.files());
        } else {
            deployment.scale(0);
            deployment.scale(replicas);
        }
    }

    private void scaleFromConfigs(ClusterDeployment deployment, ApplicationFiles files) {
        Integer replicas = yaml(Path.of(files.metadata().getMainResource().getPath())).get("spec.replicas");
        if (replicas == null) throw new OSDFException("Number of replicas is not configured for " + files.name());
        deployment.scale(replicas);
    }
}
