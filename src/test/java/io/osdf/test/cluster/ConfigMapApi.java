package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;

import java.util.regex.Matcher;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.TestCliUtils.isUnknown;
import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.util.regex.Pattern.compile;

public class ConfigMapApi extends ResourceApi {
    private final String name;

    public ConfigMapApi(String name) {
        super("configmap", name);
        this.name = name;
    }

    public static ConfigMapApi configMapApi(String name) {
        return new ConfigMapApi(name);
    }

    @Override
    public CliOutput execute(String command) {
        CliOutput resourceApiOutput = super.execute(command);
        if (!isUnknown(resourceApiOutput)) return resourceApiOutput;

        CliOutput createOutput = create(command);
        if (!isUnknown(createOutput)) return createOutput;

        return unknown();
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
