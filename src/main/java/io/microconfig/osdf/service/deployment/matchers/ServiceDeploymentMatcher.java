package io.microconfig.osdf.service.deployment.matchers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.resources.ResourceVersionInserter.resourceVersionInserter;
import static io.microconfig.osdf.service.deployment.CommonServiceDelpoyment.commonServiceDelpoyment;
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
        String version = getString(deploy, "version");
        String serviceType = getString(deploy, "service", "type");
        String deploymentResource = deploymentResource(getString(deploy, "deployment", "resource"));
        String deploymentName = deploymentName(files.name(), version, serviceType);

        preprocessIfOldType(files, version, serviceType);

        if (serviceType.contains("istio")) {
            return istioServiceDeployment(deploymentName, version, files.name(), deploymentResource, cli);
        } else if (serviceType.contains("common")) {
            return commonServiceDelpoyment(deploymentName,
                    version, files.name(),
                    deploymentResource, cli, files.resources());
        }
        return defaultServiceDeployment(deploymentName, version, files.name(), deploymentResource, cli);
    }

    private String deploymentResource(String deploymentResource) {
        return deploymentResource.equals("null") ? "dc" : deploymentResource;
    }

    private void preprocessIfOldType(ServiceFiles files, String version, String serviceType) {
        if (serviceType.contains("old") || serviceType.equals("null")) {
            resourceVersionInserter(files.root(), serviceType.contains("istio") ? version : null)
                    .insert();
        }
    }

    private String deploymentName(String name, String version, String serviceType) {
        return serviceType.contains("istio") ? name + "." + version : name;
    }
}
