package io.osdf.core.connection.cli;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.exceptions.PossibleBugException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.List.of;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
public class CliOutput {
    public static int TIMEOUT_STATUS_CODE = -1;

    private final String errorOutput;
    private final String standardOutput;
    @Getter
    private final int statusCode;

    public static CliOutput output(String standardOutput) {
        return new CliOutput("", standardOutput, 0);
    }

    public static CliOutput errorOutput(String errorOutput, int statusCode) {
        return new CliOutput(errorOutput, "", statusCode);
    }

    public static CliOutput outputOf(String command, int timeout) {
        try {
            Process process = new ProcessBuilder("/bin/sh", "-c", command).start();
            if (!waitExecution(timeout, process)) return errorOutput("Timed out", TIMEOUT_STATUS_CODE);

            if (process.exitValue() != 0) {
                return errorOutput(IOUtils.toString(process.getErrorStream(), UTF_8.name()), process.exitValue());
            }
            return output(IOUtils.toString(process.getInputStream(), UTF_8.name()));
        } catch (IOException | InterruptedException e) {
            currentThread().interrupt();
            throw new PossibleBugException("Couldn't execute cli command", e);
        }
    }

    private static boolean waitExecution(int timeoutInSec, Process process) throws InterruptedException {
        if (timeoutInSec == 0) {
            process.waitFor();
            return true;
        }
        if (!process.waitFor(timeoutInSec, SECONDS)) {
            process.destroy();
            return false;
        }
        return true;
    }

    public CliOutput throwExceptionIfError() {
        if (statusCode != 0) throw new OSDFException("Error executing cli command. Output:\n" + errorOutput);
        return this;
    }

    public List<String> getOutputLines() {
        return of(getOutput().split("\n"));
    }

    public String getOutput() {
        return statusCode == 0 ? standardOutput : errorOutput;
    }

    public boolean ok() {
        return statusCode == 0;
    }
}
