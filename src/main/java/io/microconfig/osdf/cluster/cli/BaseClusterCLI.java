package io.microconfig.osdf.cluster.cli;

import io.microconfig.osdf.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.commandline.CommandLineOutput.outputOf;
import static io.microconfig.utils.Logger.info;
import static java.lang.System.getenv;

@RequiredArgsConstructor
public class BaseClusterCLI implements ClusterCLI {
    private final boolean logEnabled;

    public static BaseClusterCLI baseClusterCLI() {
        return new BaseClusterCLI("true".equals(getenv("OSDF_LOG_CLUSTER")));
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
