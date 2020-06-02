package io.microconfig.osdf.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
@EqualsAndHashCode
public class ClusterResourceImpl implements ClusterResource {
    private final String kind;
    private final String name;

    public static ClusterResourceImpl fromOpenShiftNotation(String notation) {
        String[] split = notation.split("/");
        if (split.length != 2) {
            throw new OSDFException("Wrong notation format: " + notation);
        }
        String kind = split[0].split("\\.")[0].toLowerCase();
        String name = split[1];
        return new ClusterResourceImpl(kind, name);
    }

    public static ClusterResourceImpl fromPath(Path path) {
        Map<String, Object> resource = loadFromPath(path);
        String kind = getString(resource,"kind").toLowerCase();
        String name = getString(resource,"metadata", "name");
        return new ClusterResourceImpl(kind, name);
    }

    @Override
    public String kind() {
        return kind;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void delete(ClusterCLI cli) {
        cli.execute("delete " + kind + " " + name);
    }
}
