package io.cluster.old.cluster.openshift;

import io.cluster.old.cluster.cli.BaseClusterCli;
import io.cluster.old.cluster.cli.ClusterCli;
import io.cluster.old.cluster.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.cli.BaseClusterCli.baseClusterCLI;
import static io.cluster.old.cluster.openshift.OpenShiftAuthentication.openShiftAuthentication;

@RequiredArgsConstructor
public class OpenShiftCli implements ClusterCli {
    private final OsdfPaths paths;
    private final BaseClusterCli cli;

    public static OpenShiftCli oc(OsdfPaths paths) {
        return new OpenShiftCli(paths, baseClusterCLI());
    }

    public static OpenShiftCli oc(ClusterCli cli) {
        if (cli instanceof OpenShiftCli) return (OpenShiftCli) cli;
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
