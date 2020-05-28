package io.microconfig.osdf.cluster.openshift;

import io.microconfig.osdf.cluster.cli.BaseClusterCLI;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.cluster.cli.BaseClusterCLI.baseClusterCLI;
import static io.microconfig.osdf.cluster.openshift.OpenShiftAuthentication.openShiftAuthentication;

@RequiredArgsConstructor
public class OpenShiftCLI implements ClusterCLI {
    private final OSDFPaths paths;
    private final BaseClusterCLI cli;

    public static OpenShiftCLI oc(OSDFPaths paths) {
        return new OpenShiftCLI(paths, baseClusterCLI());
    }

    public static OpenShiftCLI oc(ClusterCLI cli) {
        if (cli instanceof OpenShiftCLI) return (OpenShiftCLI) cli;
        throw new OSDFException("OpenShift cli is required");
    }

    @Override
    public CommandLineOutput execute(String command) {
        return cli.execute("oc " + stripCLIName(command));
    }

    @Override
    public void executeAndForget(String command) {
        cli.executeAndForget("oc " + stripCLIName(command));
    }

    @Override
    public void login() {
        openShiftAuthentication(paths, this).connect();
    }

    private String stripCLIName(String command) {
        return command
                .replace("oc ", "")
                .replace("kubectl ", "");
    }
}
