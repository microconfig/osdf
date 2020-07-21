package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.ArrayList;
import java.util.List;

import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.util.function.Predicate.not;


public class ApiAggregator extends TestCli {
    private final List<ClusterCli> apis = new ArrayList<>();

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
                .orElse(unknown());
    }
}
