package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.PropertiesApi.propertiesApi;
import static io.osdf.test.cluster.TestCliUtils.executeUsing;
import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.util.regex.Pattern.compile;

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
        return executeUsing(command, List.of(super::execute, this::create, propertiesApi::execute));
    }

    @Override
    public ConfigMapApi ignoreOtherGets(boolean ignore) {
        super.ignoreOtherGets(ignore);
        propertiesApi.ignoreOtherGets(ignore);
        return this;
    }

    private CliOutput create(String command) {
        Matcher matcher = compile("create configmap (.*?) (.*)").matcher(command);
        if (!matcher.matches()) return unknown();

        String name = matcher.group(1);
        if (!name.equals(this.name)) return output("ok");

        if (exists()) return errorOutput("already exists", 1);

        update();

        return output("ok");
    }
}
