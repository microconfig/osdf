package io.microconfig.osdf.service.deployment.matchers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.service.deployment.DefaultServiceDeployment.defaultServiceDeployment;
import static io.microconfig.osdf.service.deployment.istio.DefaultIstioServiceDeployment.istioServiceDeployment;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class ServiceDeploymentMatcher {
    private final ClusterCLI cli;

    public static ServiceDeploymentMatcher serviceDeploymentMatcher(ClusterCLI cli) {
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
