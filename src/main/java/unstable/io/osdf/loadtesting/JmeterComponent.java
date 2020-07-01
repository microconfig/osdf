package unstable.io.osdf.loadtesting;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.LocalClusterResource;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.core.deployment.ServiceDeploymentMatcher;
import io.osdf.core.service.local.DefaultServiceFiles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.core.local.component.finder.MicroConfigComponentsFinder.componentsFinder;
import static io.osdf.core.service.cluster.types.DefaultClusterService.defaultClusterService;
import static io.osdf.actions.info.healthcheck.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.osdf.core.service.local.DefaultServiceFiles.serviceFiles;
import static io.microconfig.utils.Logger.announce;

@Getter
@RequiredArgsConstructor
public class JmeterComponent {
    private final String componentName;
    private final Path componentPath;
    private final ClusterCli cli;

    public static JmeterComponent jmeterComponent(String componentName, Path componentPath, ClusterCli cli) {
        return new JmeterComponent(componentName, componentPath, cli);
    }

    public void deploy() {
        List<LocalClusterResource> resources = getJmeterServiceFiles().resources();
        resources.stream()
                .filter(this::exists)
                .forEach(clusterResource -> clusterResource.delete(cli));
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
        return successfulDeploymentChecker().check(deploymentsFromServiceFiles(), getJmeterServiceFiles());
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
