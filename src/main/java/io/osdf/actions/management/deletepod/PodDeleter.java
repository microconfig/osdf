package io.osdf.actions.management.deletepod;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.announce;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static io.osdf.core.cluster.pod.Pod.fromPods;
import static java.util.List.of;

@RequiredArgsConstructor
public class PodDeleter {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static PodDeleter podDeleter(OsdfPaths paths, ClusterCli cli) {
        return new PodDeleter(paths, cli);
    }

    public void delete(String componentName, List<String> podNames) {
        ServiceApplication service = activeRequiredAppsLoader(paths, of(componentName))
                .load(service(cli)).stream()
                .findFirst()
                .orElseThrow(() -> new OSDFException(componentName + " not found"));
        podNames.forEach(podName -> deletePods(service.deployment(), podNames));
    }

    private void deletePods(ClusterDeployment deployment, List<String> podNames) {
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
