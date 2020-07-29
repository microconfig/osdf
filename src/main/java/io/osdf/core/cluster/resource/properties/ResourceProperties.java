package io.osdf.core.cluster.resource.properties;

import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class ResourceProperties {
    private final Map<String, String> properties;

    public static Optional<ResourceProperties> resourceProperties(ClusterCli cli, ClusterResource resource, Map<String, String> keyMap) {
        String query = keyMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":." + entry.getValue())
                .collect(joining(","));

        CliOutput output = cli.execute("get " + resource.kind() + " " + resource.name() + " -o custom-columns=" + query);
        List<String> outputLines = output.getOutputLines();
        if (!output.ok() || outputLines.size() != 2) return empty();

        String[] keys = outputLines.get(0).split("\\s+");
        String[] properties = outputLines.get(1).split("\\s+");
        Map<String, String> result = range(0, keys.length)
                .boxed()
                .collect(toUnmodifiableMap(i -> keys[i], i -> properties[i]));
        return Optional.of(new ResourceProperties(result));
    }

    public String get(String key) {
        return properties.get(key);
    }

}
