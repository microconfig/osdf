package io.microconfig.osdf.commands;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.cluster.old.cluster.pod.Pod;
import io.osdf.settings.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;
import static io.cluster.old.cluster.pod.Pod.fromPods;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class LogsCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;


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
