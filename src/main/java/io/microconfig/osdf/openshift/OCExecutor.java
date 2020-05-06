package io.microconfig.osdf.openshift;

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
public class OCExecutor {
    private final OSDFPaths paths;
    private final boolean logOc;

    public static OCExecutor oc(OSDFPaths paths) {
        return new OCExecutor(paths, "true".equals(getenv("OSDF_LOG_OC")));
    }

    public CommandLineOutput execute(String command) {
        log(command);
        CommandLineOutput output = outputOf(command);
        log(output.toString());
        throwIfOpenShiftError(output.toString());
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
