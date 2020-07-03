package io.microconfig.osdf.service.deployment.tools;

import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.utils.YamlUtils;

import java.util.Map;

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
        Map<String, Object> deploy = loadFromPath(files.getPath("mainResource"));
        Integer replicas = YamlUtils.get(deploy, "spec.replicas");
        deployment.scale(replicas);
    }
}
