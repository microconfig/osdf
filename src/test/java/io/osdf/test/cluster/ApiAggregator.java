package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.ArrayList;
import java.util.List;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.util.function.Predicate.not;


public class ApiAggregator extends TestCli {
    private final List<ClusterCli> apis = new ArrayList<>();
    private boolean answerNotFound = true;

    public static ApiAggregator apis() {
        return new ApiAggregator();
    }

    public ApiAggregator add(ClusterCli cli) {
        apis.add(cli);
        return this;
    }

    @Override
    public CliOutput execute(String command) {
       return apis.stream()
                .map(api -> api.execute(command))
                .filter(not(TestCliUtils::isUnknown))
                .findFirst()
                .orElse(defaultAnswer(command));
    }

    private CliOutput defaultAnswer(String command) {
        if (!answerNotFound) return unknown();
        if (command.startsWith("get") || command.startsWith("delete")) return errorOutput("not found", 1);
        return unknown();
    }
}
