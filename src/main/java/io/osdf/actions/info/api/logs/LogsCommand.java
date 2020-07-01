package io.osdf.actions.info.api.logs;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.osdf.core.cluster.pod.Pod.fromPods;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class LogsCommand {
    private final OsdfPaths paths;
    private final ClusterCli cli;


    public void show(String serviceName, String podName) {
        ServiceDeployment deployment = serviceLoader(paths, cli)
                .loadByName(serviceName)
                .deployment();

        Pod pod = fromPods(deployment.pods(), podName);
        if (pod == null) {
            error("Pod " + podName + " not found");
            return;
        }
        pod.logs();
    }
}
