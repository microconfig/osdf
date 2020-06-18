package io.cluster.old.cluster.openshift;

import io.cluster.old.cluster.cli.BaseClusterCLI;
import io.cluster.old.cluster.cli.ClusterCLI;
import io.cluster.old.cluster.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import io.osdf.settings.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.cli.BaseClusterCLI.baseClusterCLI;
import static io.cluster.old.cluster.openshift.OpenShiftAuthentication.openShiftAuthentication;

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
    public void login() {
        openShiftAuthentication(paths, this).connect();
    }

    private String stripCLIName(String command) {
        return command
                .replace("oc ", "")
                .replace("kubectl ", "");
    }
}
