package io.osdf.test.cluster.api;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;

import java.util.Map;
import java.util.regex.Matcher;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.api.PropertiesApi.propertiesApi;

public class ConfigMapApi extends ResourceApi {
    private final String name;
    private final PropertiesApi propertiesApi;

    public ConfigMapApi(String name) {
        super("configmap", name);
        this.name = name;
        this.propertiesApi = propertiesApi("configmap", name).add("default", "value");
    }

    public static ConfigMapApi configMapApi(String name) {
        return new ConfigMapApi(name);
    }

    public ConfigMapApi setContent(Map<String, String> content) {
        propertiesApi.clear();
        content.forEach((key, value) -> propertiesApi.add("data." + key, value));
        return this;
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .executor(super::execute)
                .executor(propertiesApi::execute)
                .pattern("create configmap (.*?) (.*)", this::create)
                .build().execute(command);
    }

    private CliOutput create(Matcher matcher) {
        String name = matcher.group(1);
        if (!name.equals(this.name)) return output("ok");
        if (exists()) return errorOutput("already exists", 1);

        update();
        return output("ok");
    }
}
