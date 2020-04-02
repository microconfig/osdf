package io.microconfig.osdf.openshift;

import io.microconfig.osdf.utils.CommandLineExecutor;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.factory.configtypes.StandardConfigTypes.DEPLOY;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;

@RequiredArgsConstructor
public class OCExecutor {
    private final String env;
    private final Path configPath;

    public static OCExecutor oc(String env, Path configPath) {
        return new OCExecutor(env, configPath);
    }

    public String execute(String command) {
        return CommandLineExecutor.execute(command);
    }

    public String execute(String command, boolean allowErrors) {
        return CommandLineExecutor.execute(command, allowErrors);
    }

    public List<String> executeAndReadLines(String command) {
        return CommandLineExecutor.executeAndReadLines(command);
    }

    public List<String> executeAndReadLines(String command, boolean allowErrors) {
        return CommandLineExecutor.executeAndReadLines(command, allowErrors);
    }

    public String project() {
        return propertyGetter(env, configPath).get(DEPLOY, "openshift-urls", "project");
    }
}
