package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.develop.service.deployment.ServiceDeployment;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.develop.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.osdf.openshift.Pod.fromPods;
import static io.microconfig.utils.Logger.error;

@RequiredArgsConstructor
public class LogsCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;


    public void show(String serviceName, String podName) {
        ServiceDeployment deployment = defaultServiceDeployPacksLoader(paths, cli)
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
