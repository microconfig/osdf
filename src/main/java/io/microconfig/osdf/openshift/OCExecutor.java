package io.microconfig.osdf.openshift;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.utils.CommandLineExecutor;
import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.core.configtypes.StandardConfigType.DEPLOY;
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

    public String execute(String command) {
        log(command);
        return returnOrThrowException(log(CommandLineExecutor.execute(command)));
    }

    public String execute(String command, boolean allowErrors) {
        log(command);
        return returnOrThrowException(log(CommandLineExecutor.execute(command, allowErrors)));
    }

    public List<String> executeAndReadLines(String command) {
        log(command);
        return returnOrThrowException(log(CommandLineExecutor.executeAndReadLines(command)));
    }

    public List<String> executeAndReadLines(String command, boolean allowErrors) {
        log(command);
        return returnOrThrowException(log(CommandLineExecutor.executeAndReadLines(command, allowErrors)));
    }

    public String project() {
        return propertyGetter(paths).get(DEPLOY, "openshift-urls", "project");
    }

    private String returnOrThrowException(String output) {
        throwIfUnableToConnect(output);
        return output;
    }

    private List<String> returnOrThrowException(List<String> output) {
        output.forEach(this::throwIfUnableToConnect);
        return output;
    }

    private void throwIfUnableToConnect(String output) {
        if (output.toLowerCase().contains("unable to connect")) throw new OSDFException("Unable to connect to OpenShift");
    }

    private String log(String string) {
        if (logOc) {
            info(string);
        }
        return string;
    }

    private List<String> log(List<String> strings) {
        if (logOc) {
            strings.forEach(Logger::info);
        }
        return strings;
    }
}
