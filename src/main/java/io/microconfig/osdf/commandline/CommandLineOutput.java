package io.microconfig.osdf.commandline;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.List.of;

@RequiredArgsConstructor
public class CommandLineOutput {
    private final String errorOutput;
    private final String standardOutput;
    private final int statusCode;

    public static CommandLineOutput output(String standardOutput) {
        return new CommandLineOutput("", standardOutput, 0);
    }

    public static CommandLineOutput errorOutput(String errorOutput, int statusCode) {
        return new CommandLineOutput(errorOutput, "", statusCode);
    }

    public static CommandLineOutput outputOf(String command) {
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

    public void throwExceptionIfError(RuntimeException e) {
        if (statusCode != 0) throw e;
    }

    public CommandLineOutput throwExceptionIfError() {
        throwExceptionIfError(new RuntimeException("Error executing command:\n" + errorOutput));
        return this;
    }

    public List<String> getOutputLines() {
        return of(getOutput().split("\n"));
    }

    public String getOutput() {
        return statusCode == 0 ? standardOutput : errorOutput;
    }
}
