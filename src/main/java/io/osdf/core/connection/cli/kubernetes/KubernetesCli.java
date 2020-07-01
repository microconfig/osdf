package io.osdf.core.connection.cli.kubernetes;

import io.osdf.core.connection.cli.BaseClusterCli;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.connection.cli.BaseClusterCli.baseClusterCLI;

@RequiredArgsConstructor
public class KubernetesCli implements ClusterCli {
    private final BaseClusterCli cli;

    public static KubernetesCli kubernetes() {
        return new KubernetesCli(baseClusterCLI());
    }

    @Override
    public CliOutput execute(String command) {
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
