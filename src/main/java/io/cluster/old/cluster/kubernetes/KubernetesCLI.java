package io.cluster.old.cluster.kubernetes;

import io.cluster.old.cluster.cli.BaseClusterCLI;
import io.cluster.old.cluster.cli.ClusterCLI;
import io.cluster.old.cluster.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.cli.BaseClusterCLI.baseClusterCLI;

@RequiredArgsConstructor
public class KubernetesCLI implements ClusterCLI {
    private final BaseClusterCLI cli;

    public static KubernetesCLI kubernetes() {
        return new KubernetesCLI(baseClusterCLI());
    }

    @Override
    public CommandLineOutput execute(String command) {
        return cli.execute(addKubectlPrefix(command));
    }

    @Override
    public void login() {
        throw new OSDFException("Kubernetes is not yet supported");
    }

    private String addKubectlPrefix(String command) {
        if (command.startsWith("oc ")) throw new OSDFException("Not supported for kubernetes");
        return command.startsWith("kubectl ") ? command : "kubectl " + command;
    }
}
