package io.osdf.core.service.core.deployment;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.osdf.core.service.core.deployment.types.DefaultServiceDeployment.defaultServiceDeployment;
import static io.osdf.core.service.core.deployment.types.istio.DefaultIstioServiceDeployment.istioServiceDeployment;
import static io.osdf.common.utils.YamlUtils.getString;
import static io.osdf.common.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class ServiceDeploymentMatcher {
    private final ClusterCli cli;

    public static ServiceDeploymentMatcher serviceDeploymentMatcher(ClusterCli cli) {
        return new ServiceDeploymentMatcher(cli);
    }

    public ServiceDeployment match(ServiceFiles files) {
        Map<String, Object> deploy = loadFromPath(files.getPath("deploy.yaml"));
        String version = getString(deploy, "app", "version");
        String serviceType = getString(deploy, "service", "type");
        String deploymentResource = deploymentResource(getString(deploy, "deployment" , "resource"));
        String deploymentName = deploymentName(files.name(), version, serviceType);

        if (serviceType.contains("istio")) {
            return istioServiceDeployment(deploymentName, version, files.name(), deploymentResource, cli);
        }
        return defaultServiceDeployment(deploymentName, version, files.name(), deploymentResource, cli);
    }

    private String deploymentResource(String deploymentResource) {
        return deploymentResource.equals("null") ? "dc" : deploymentResource;
    }

    private String deploymentName(String name, String version, String serviceType) {
        return serviceType.contains("istio") ? name + "." + version : name;
    }
}
