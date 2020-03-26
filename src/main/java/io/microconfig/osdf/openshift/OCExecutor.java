package io.microconfig.osdf.openshift;

import io.microconfig.osdf.utils.CommandLineExecutor;

import java.util.List;

public class OCExecutor {
    public static OCExecutor oc() {
        return new OCExecutor();
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
}
