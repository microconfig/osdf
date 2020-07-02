package io.osdf.core.connection.cli;

import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.List.of;

@RequiredArgsConstructor
public class CliOutput {
    private final String errorOutput;
    private final String standardOutput;
    private final int statusCode;

    public static CliOutput output(String standardOutput) {
        return new CliOutput("", standardOutput, 0);
    }

    public static CliOutput errorOutput(String errorOutput, int statusCode) {
        return new CliOutput(errorOutput, "", statusCode);
    }

    public static CliOutput outputOf(String command) {
        try {
            Process process = new ProcessBuilder("/bin/sh", "-c", command).start();
            process.waitFor();

            if (process.exitValue() != 0) {
                return errorOutput(IOUtils.toString(process.getErrorStream(), UTF_8.name()), process.exitValue());
            }
            return output(IOUtils.toString(process.getInputStream(), UTF_8.name()));
        } catch (IOException | InterruptedException e) {
            currentThread().interrupt();
            throw new RuntimeException("Couldn't execute command: " + command, e);
        }
    }

    public void consumeOutput(Consumer<String> consumer) {
        consumer.accept(getOutput());
    }

    public CliOutput throwExceptionIfError() {
        if (statusCode != 0) throw new OSDFException("Error executing command:\n" + errorOutput);
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
