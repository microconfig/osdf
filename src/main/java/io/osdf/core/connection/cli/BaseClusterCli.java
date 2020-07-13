package io.osdf.core.connection.cli;

import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.info;
import static io.osdf.core.connection.cli.CliOutput.outputOf;
import static java.lang.System.getenv;

@RequiredArgsConstructor
public class BaseClusterCli implements ClusterCli {
    private final boolean logEnabled;

    public static BaseClusterCli baseClusterCLI() {
        return new BaseClusterCli("true".equals(getenv("OSDF_LOG_CLUSTER")));
    }

    @Override
    public CliOutput execute(String command) {
        return execute(command, 0);
    }

    @Override
    public CliOutput execute(String command, int timeoutInSec) {
        log(command);
        CliOutput output = outputOf(command, timeoutInSec);
        log(output.getOutput());
        throwIfClusterError(output.getOutput());
        return output;
    }

    @Override
    public void login() {
        throw new OSDFException("Specify cluster context");
    }

    @Override
    public void logout() {
        //no login => no logout
    }

    private void throwIfClusterError(String output) {
        if (output.toLowerCase().contains("unable to connect")) throw new OSDFException("Unable to connect to cluster: " + output);
    }

    private void log(String string) {
        if (logEnabled) {
            info(string);
        }
    }
}
