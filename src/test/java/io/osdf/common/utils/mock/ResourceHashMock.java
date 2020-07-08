package io.osdf.common.utils.mock;

import io.osdf.core.connection.cli.CliOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static io.osdf.core.connection.cli.CliOutput.output;
import static java.util.regex.Pattern.compile;

public class ResourceHashMock implements OCMock {
    private final Map<String, String> hashes = new HashMap<>();

    public static ResourceHashMock resourceHashMock() {
        return new ResourceHashMock();
    }

    public ResourceHashMock addResourceHash(String kind, String name, String hash) {
        hashes.put(resourceKey(kind, name), hash);
        return this;
    }

    @Override
    public CliOutput execute(String command) {
        Matcher matcher = compile(pattern()).matcher(command);
        if (!matcher.matches()) throw new RuntimeException("Wrong command");

        String kind = matcher.group(1);
        String name = matcher.group(2);
        if (!hashes.containsKey(resourceKey(kind, name))) return formatOutput("<none>");
        return formatOutput(hashes.get(resourceKey(kind, name)));
    }

    @Override
    public String pattern() {
        return "oc get (.*) (.*) -o custom-columns=\"hash:\\.metadata\\.labels\\.configHash\"";
    }

    private CliOutput formatOutput(String s) {
        return output("hash\n" + s);
    }

    private String resourceKey(String kind, String name) {
        return kind + "-" + name;
    }
}