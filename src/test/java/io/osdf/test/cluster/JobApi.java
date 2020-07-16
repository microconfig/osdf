package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;
import lombok.RequiredArgsConstructor;

import static io.osdf.test.cluster.PropertiesApi.propertiesApi;
import static io.osdf.test.cluster.ResourceApi.resourceApi;
import static io.osdf.test.cluster.TestCliUtils.executeUsing;
import static java.util.List.of;

@RequiredArgsConstructor
public class JobApi extends TestCli {
    private final String name;
    private final PropertiesApi propertiesApi;
    private final ResourceApi jobResourceApi;

    public static JobApi jobApi(String name) {
        PropertiesApi propertiesApi = propertiesApi("job", name);
        propertiesApi.add("status.succeeded", "1");

        ResourceApi jobResourceApi = resourceApi("job", name);

        return new JobApi(name, propertiesApi, jobResourceApi);
    }

    public JobApi ignoreOtherGets(boolean ignore) {
        jobResourceApi.ignoreOtherGets(ignore);
        propertiesApi.ignoreOtherGets(ignore);
        return this;
    }

    @Override
    public CliOutput execute(String command) {
        return executeUsing(command, of(propertiesApi::execute, jobResourceApi::execute));
    }
}
