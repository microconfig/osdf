package io.osdf.test.cluster.api;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;
import io.osdf.test.cluster.TestCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.test.cluster.api.PropertiesApi.propertiesApi;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;

@RequiredArgsConstructor
public class JobApi extends TestCli {
    private final PropertiesApi propertiesApi;
    private final ResourceApi jobResourceApi;

    public static JobApi jobApi(String name) {
        PropertiesApi propertiesApi = propertiesApi("job", name);
        propertiesApi.add("status.succeeded", "1");

        ResourceApi jobResourceApi = resourceApi("job", name);

        return new JobApi(propertiesApi, jobResourceApi);
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .executor(propertiesApi::execute)
                .executor(jobResourceApi::execute)
                .build().execute(command);
    }
}
