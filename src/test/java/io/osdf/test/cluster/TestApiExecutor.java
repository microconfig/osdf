package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;

import static io.osdf.test.cluster.TestCliUtils.isUnknown;
import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.compile;

@Builder
public class TestApiExecutor {
    @Singular
    private final List<Function<String, CliOutput>> executors;
    @Singular
    private final Map<String, Function<Matcher, CliOutput>> patterns;

    public CliOutput execute(String command) {
        CliOutput executorsOutput = executors.stream()
                .map(api -> api.apply(command))
                .filter(not(TestCliUtils::isUnknown))
                .findFirst()
                .orElse(unknown());
        if (!isUnknown(executorsOutput)) return executorsOutput;

        String matchedPattern = patterns.keySet().stream()
                .filter(pattern -> compile(pattern).matcher(command).matches())
                .findFirst()
                .orElse(null);
        if (matchedPattern == null) return unknown();
        Matcher matcher = compile(matchedPattern).matcher(command);
        if (!matcher.matches()) return unknown();
        return patterns.get(matchedPattern).apply(matcher);
    }
}
