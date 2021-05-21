package io.osdf.core.connection.cli.openshift;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.BaseClusterCli;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.connection.cli.BaseClusterCli.baseClusterCLI;
import static io.osdf.core.connection.cli.openshift.OpenShiftAuthentication.openShiftAuthentication;

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
    public CliOutput execute(String command) {
        return cli.execute("oc " + stripCLIName(command));
    }

    @Override
    public CliOutput execute(String command, int timeoutInSec) {
        return cli.execute("oc " + stripCLIName(command), timeoutInSec);
    }

    @Override
    public void login() {
        openShiftAuthentication(paths, this).connect();
    }

    @Override
    public void logout() {
        cli.execute("oc logout");
    }

    private String stripCLIName(String command) {
        return command
                .replace("oc ", "")
                .replace("kubectl ", "");
    }
}
