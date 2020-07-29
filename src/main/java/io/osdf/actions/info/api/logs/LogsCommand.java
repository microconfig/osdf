package io.osdf.actions.info.api.logs;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static io.osdf.core.cluster.pod.Pod.fromPods;
import static java.util.List.of;

@RequiredArgsConstructor
public class LogsCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public void show(String serviceName, String podName) {
        ClusterDeployment deployment = activeRequiredAppsLoader(paths, of(serviceName))
                .load(service(cli)).stream()
                .findFirst()
                .orElseThrow(() -> new OSDFException(serviceName + " not found"))
                .getDeploymentOrThrow();

        Pod pod = fromPods(deployment.pods(), podName);
        if (pod == null) {
            error("Pod " + podName + " not found");
            return;
        }
        pod.logs(serviceName);
    }
}
