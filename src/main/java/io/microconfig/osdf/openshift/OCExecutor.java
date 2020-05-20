package io.microconfig.osdf.openshift;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commandline.CommandLineOutput;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.core.configtypes.StandardConfigType.DEPLOY;
import static io.microconfig.osdf.commandline.CommandLineOutput.outputOf;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.utils.Logger.info;
import static java.lang.System.getenv;

@RequiredArgsConstructor
public class OCExecutor implements ClusterCLI {
    private final OSDFPaths paths;
    private final boolean logOc;

    public static OCExecutor oc(OSDFPaths paths) {
        return new OCExecutor(paths, "true".equals(getenv("OSDF_LOG_OC")));
    }

    public static OCExecutor oc(ClusterCLI cli) {
        if (cli instanceof OCExecutor) return (OCExecutor) cli;
        throw new OSDFException("OpenShift cli is required");
    }

    public CommandLineOutput execute(String command) {
        log(command);
        CommandLineOutput output = outputOf(command);
        log(output.getOutput());
        throwIfOpenShiftError(output.getOutput());
        return output;
    }

    public String project() {
        return propertyGetter(paths).get(DEPLOY, "openshift-urls", "project");
    }

    private void throwIfOpenShiftError(String output) {
        if (output.toLowerCase().contains("unable to connect")) throw new OSDFException("Unable to connect to OpenShift");
    }

    private void log(String string) {
        if (logOc) {
            info(string);
        }
    }
}
