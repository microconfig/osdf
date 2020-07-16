package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;

import java.util.List;
import java.util.function.Function;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static java.util.function.Predicate.not;

public class TestCliUtils {
    public static final String UNKNOWN = "unknown command";

    public static boolean isUnknown(CliOutput output) {
        return output.getOutput().equals(UNKNOWN);
    }

    public static CliOutput unknown() {
        return errorOutput(UNKNOWN, 1);
    }

    public static CliOutput executeUsing(String command, List<Function<String, CliOutput>> apis) {
        return apis.stream()
                .map(api -> api.apply(command))
                .filter(not(TestCliUtils::isUnknown))
                .findFirst()
                .orElse(unknown());
    }
}
