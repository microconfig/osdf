package io.cluster.old.cluster.cli;

import io.cluster.old.cluster.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.commandline.CommandLineOutput.outputOf;
import static io.microconfig.utils.Logger.info;
import static java.lang.System.getenv;

@RequiredArgsConstructor
public class BaseClusterCli implements ClusterCli {
    private final boolean logEnabled;

    public static BaseClusterCli baseClusterCLI() {
        return new BaseClusterCli("true".equals(getenv("OSDF_LOG_CLUSTER")));
    }

    @Override
    public CommandLineOutput execute(String command) {
        log(command);
        CommandLineOutput output = outputOf(command);
        log(output.getOutput());
        throwIfClusterError(output.getOutput());
        return output;
    }

    @Override
    public void login() {
        throw new OSDFException("Specify cluster context");
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
