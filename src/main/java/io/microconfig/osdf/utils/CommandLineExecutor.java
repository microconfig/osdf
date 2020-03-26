package io.microconfig.osdf.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import static io.microconfig.utils.Logger.error;
import static java.lang.Runtime.getRuntime;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.List.of;

public class CommandLineExecutor {
    public static String execute(String command) {
        return execute(command, false);
    }

    public static List<String> executeAndReadLines(String command) {
        return executeAndReadLines(command, false);
    }

    public static List<String> executeAndReadLines(String command, boolean allowErrors) {
        return of(execute(command, allowErrors).split("\n"));
    }

    public static String execute(String command, boolean allowErrors) {
        try {
            Process process = getRuntime().exec(command);
            process.waitFor();

            if (process.exitValue() != 0) {
                String errorOutput = IOUtils.toString(process.getErrorStream(), UTF_8.name());
                if (allowErrors) return errorOutput;
                error(errorOutput);
                throw new RuntimeException("Non-zero exit code (" + process.exitValue() + ") of command: " + command);
            }

            return IOUtils.toString(process.getInputStream(), UTF_8.name());
        } catch (IOException | InterruptedException e) {
            currentThread().interrupt();
            throw new RuntimeException("Couldn't execute command: " + command, e);
        }
    }
}