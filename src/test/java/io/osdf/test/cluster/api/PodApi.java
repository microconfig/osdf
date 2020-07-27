package io.osdf.test.cluster.api;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;
import io.osdf.test.cluster.TestCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.test.cluster.api.PropertiesApi.propertiesApi;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;

@RequiredArgsConstructor
public class PodApi extends TestCli {
    private final ResourceApi resourceApi;
    private final PropertiesApi propertiesApi;

    public static PodApi podApi(String name) {
        PropertiesApi propertiesApi = propertiesApi("pod", name);
        propertiesApi.add("status.conditions[?(@.type == \\\"Ready\\\")].status", "True");
        propertiesApi.add("spec.containers[].name", "first\nsecond");

        return new PodApi(resourceApi("pod", name), propertiesApi);
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .executor(resourceApi::execute)
                .executor(propertiesApi::execute)
                .build().execute(command);
    }

    public boolean exists() {
        return resourceApi.exists();
    }
}
