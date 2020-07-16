package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class PropertiesApi extends TestCli {
    private final String kind;
    private final String name;

    private Map<String, String> properties = new HashMap<>();
    @Setter
    private boolean ignoreOtherGets = false;

    public static PropertiesApi propertiesApi(String kind, String name) {
        return new PropertiesApi(kind, name);
    }

    public PropertiesApi add(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public void clear() {
        properties.clear();
    }

    @Override
    public CliOutput execute(String command) {
        Matcher matcher = compile("get\\s(.*)\\s(.*)\\s-o custom-columns=(.*)").matcher(command);
        if (!matcher.matches()) return unknown();

        String kind = matcher.group(1);
        String name = matcher.group(2);
        if (!kind.equals(this.kind) || !name.equals(this.name)) {
            return ignoreOtherGets ? unknown() : errorOutput("not found", 1);
        }

        Map<String, String> queriedProperties = getPropertiesInQuery(matcher);

        List<String> resultAliases = new ArrayList<>();
        List<String> resultValues = new ArrayList<>();
        queriedProperties.forEach((alias, key) -> {
            String value = properties.get(key);
            resultAliases.add(alias);
            resultValues.add(value == null ? "<none>" : value);
        });

        return output(join("\t", resultAliases) + "\n" + join("\t", resultValues));
    }

    private Map<String, String> getPropertiesInQuery(Matcher matcher) {
        String query = matcher.group(3).replace("\"", "");
        String[] fields = query.split(",");
        Map<String, String> queriedProperties = new HashMap<>();
        stream(fields)
                .map(field -> field.split(":"))
                .forEach(aliasAndKey -> queriedProperties.put(removeDot(aliasAndKey[0]), removeDot(aliasAndKey[1])));
        return queriedProperties;
    }

    private String removeDot(String s) {
        return s.startsWith(".") ? s.substring(1) : s;
    }
}