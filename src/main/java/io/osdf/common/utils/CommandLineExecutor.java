package io.osdf.common.utils;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.exceptions.PossibleBugException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import static io.microconfig.utils.Logger.error;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

public class CommandLineExecutor {
    public static String execute(String command) {
        return execute(command, emptyList());
    }

    public static String execute(String command, List<String> credentials) {
        try {
            Process process = new ProcessBuilder("/bin/sh", "-c", command).start();
            process.waitFor();

            if (process.exitValue() != 0) {
                String errorOutput = IOUtils.toString(process.getErrorStream(), UTF_8.name());
                error(errorOutput);
                throw new OSDFException("Non-zero exit code (" + process.exitValue() + ") of command: " + hideCredentials(command, credentials));
            }
            return IOUtils.toString(process.getInputStream(), UTF_8.name());
        } catch (IOException | InterruptedException e) {
            currentThread().interrupt();
            throw new PossibleBugException("Couldn't execute command: " + hideCredentials(command, credentials), e);
        }
    }

    private static String hideCredentials(String target, List<String> credentials) {
        String result = target;
        for (String credential : credentials) {
            result = result.replace(credential, "***");
        }
        return result;
    }
}