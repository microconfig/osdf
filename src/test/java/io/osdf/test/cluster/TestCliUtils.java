package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;

public class TestCliUtils {
    public static final String UNKNOWN = "unknown command";

    public static boolean isUnknown(CliOutput output) {
        return output.getOutput().equals(UNKNOWN);
    }

    public static CliOutput unknown() {
        return errorOutput(UNKNOWN, 1);
    }
}
