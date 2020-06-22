package io.microconfig.osdf.cluster.kubernetes;

import io.microconfig.osdf.cluster.cli.BaseClusterCLI;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.cluster.cli.BaseClusterCLI.baseClusterCLI;

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

    @Override
    public void logout() {
        throw new OSDFException("Kubernetes is not yet supported");
    }

    private String addKubectlPrefix(String command) {
        if (command.startsWith("oc ")) throw new OSDFException("Not supported for kubernetes");
        return command.startsWith("kubectl ") ? command : "kubectl " + command;
    }
}
