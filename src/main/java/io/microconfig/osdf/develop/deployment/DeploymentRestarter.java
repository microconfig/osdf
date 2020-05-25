package io.microconfig.osdf.develop.deployment;

import io.microconfig.osdf.develop.service.ServiceFiles;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

public class DeploymentRestarter {
    public static DeploymentRestarter deploymentRestarter() {
        return new DeploymentRestarter();
    }

    public void restart(ServiceDeployment deployment, ServiceFiles files) {
        int replicas = deployment.info().replicas();
        if (replicas > 0) {
            deployment.scale(0);
            deployment.scale(replicas);
        } else {
            scaleFromConfigs(deployment, files);
        }
    }

    private void scaleFromConfigs(ServiceDeployment deployment, ServiceFiles files) {
        Map<String, Object> deploy = loadFromPath(files.getPath("deploy.yaml"));
        Integer replicas = getInt(deploy, "replicas.count");
        deployment.scale(replicas);
    }
}
