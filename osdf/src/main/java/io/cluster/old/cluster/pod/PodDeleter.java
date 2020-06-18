package io.cluster.old.cluster.pod;

import io.cluster.old.cluster.cli.ClusterCli;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.cluster.old.cluster.pod.Pod.fromPods;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class PodDeleter {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static PodDeleter podDeleter(OsdfPaths paths, ClusterCli cli) {
        return new PodDeleter(paths, cli);
    }

    public void delete(String componentName, List<String> podNames) {
        ServiceDeployPack deployPack = serviceLoader(paths, cli).loadByName(componentName);
        podNames.forEach(podName -> deletePods(deployPack.deployment(), podNames));
    }

    private void deletePods(ServiceDeployment deployment, List<String> podNames) {
        List<Pod> pods = deployment.pods();
        podNames.stream()
                .map(podName -> fromPods(pods, podName))
                .forEach(this::deletePod);
    }

    private void deletePod(Pod pod) {
        pod.delete();
        announce("Deleted pod " + pod.getName());
    }
}
