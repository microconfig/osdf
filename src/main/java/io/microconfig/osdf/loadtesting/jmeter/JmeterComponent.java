package io.microconfig.osdf.loadtesting.jmeter;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.cluster.resource.ClusterResource;
import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.matchers.ServiceDeploymentMatcher;
import io.microconfig.osdf.service.files.DefaultServiceFiles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.microconfig.osdf.service.DefaultClusterService.defaultClusterService;
import static io.microconfig.osdf.service.deployment.checkers.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.microconfig.osdf.service.files.DefaultServiceFiles.serviceFiles;
import static io.microconfig.utils.Logger.announce;

@Getter
@RequiredArgsConstructor
public class JmeterComponent {
    private final String componentName;
    private final Path componentPath;
    private final ClusterCLI cli;

    public static JmeterComponent jmeterComponent(String componentName, Path componentPath, ClusterCLI cli) {
        return new JmeterComponent(componentName, componentPath, cli);
    }

    public void deploy() {
        List<LocalClusterResource> resources = getJmeterServiceFiles().resources();
        resources.forEach(clusterResource -> {
            if (exists(clusterResource)) {
                clusterResource.delete(cli);
            }
        });
        defaultClusterService(componentName, deploymentsFromServiceFiles().version(), cli).upload(resources);
        if (!checkDeploy()) {
            announce(componentName + " hasn't been started. Please wait a cleaning resources.");
            throw new OSDFException(componentName + " hasn't been started");
        }
    }

    public String getServiceIp() {
        String command = "oc get service " + componentName + "-service -o custom-columns=CLUSTER-IP:.spec.clusterIP";
        List<String> output = cli.execute(command).getOutputLines();
        if (output.get(0).toLowerCase().contains("not found"))
            throw new OSDFException("Service " + componentName + " ip not found");
        return output.get(1).strip();
    }

    public void deleteAll() {
        cli.execute("oc delete all -l application=" + componentName).throwExceptionIfError();
    }

    public boolean checkDeploy() {
        DefaultServiceFiles jmeterServiceFiles = getJmeterServiceFiles();
        return successfulDeploymentChecker().check(deploymentsFromServiceFiles(), jmeterServiceFiles);
    }

    public List<Pod> pods() {
        return deploymentsFromServiceFiles().pods();
    }

    private boolean exists(ClusterResource clusterResource) {
        List<String> output = cli.execute("oc get " + clusterResource.kind() + " " + clusterResource.name())
                .getOutputLines();
        return !output.get(0).toLowerCase().contains("no resources found");
    }

    private ServiceDeployment deploymentsFromServiceFiles() {
        ServiceDeploymentMatcher matcher = ServiceDeploymentMatcher.serviceDeploymentMatcher(cli);
        return matcher.match(getJmeterServiceFiles());
    }

    private DefaultServiceFiles getJmeterServiceFiles() {
        return serviceFiles(componentsFinder(componentPath.getParent()).findByName(componentName));
    }
}
