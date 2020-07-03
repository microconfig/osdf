package io.microconfig.osdf.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static java.util.Objects.hash;

@RequiredArgsConstructor
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
    public String label(ClusterCLI cli, String key) {
        return cli.execute("get " + kind + " " + name + " -o custom-columns=\"label:.metadata.labels." + key + "\"")
                .throwExceptionIfError()
                .getOutputLines()
                .get(1)
                .strip();
    }

    @Override
    public void delete(ClusterCLI cli) {
        cli.execute("delete " + kind + " " + name);
    }

    @Override
    public String toString() {
        return kind + "/" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClusterResource)) return false;
        ClusterResource that = (ClusterResource) o;
        return Objects.equals(kind, that.kind()) && Objects.equals(name, that.name());
    }

    @Override
    public int hashCode() {
        return hash(kind, name);
    }
}
