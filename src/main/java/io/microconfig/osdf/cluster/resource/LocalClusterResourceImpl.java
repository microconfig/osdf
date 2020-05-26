package io.microconfig.osdf.cluster.resource;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.cluster.resource.ClusterResourceImpl.fromPath;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
@EqualsAndHashCode(of = {"path"})
public class LocalClusterResourceImpl implements LocalClusterResource {
    private final Path path;
    private final ClusterResourceImpl clusterResource;

    public static LocalClusterResourceImpl localClusterResource(Path path) {
        return new LocalClusterResourceImpl(path, fromPath(path));
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public void upload(ClusterCLI cli) {
        if (getRemoteHash(cli).equals(getLocalHash())) return;
        cli.execute("oc apply -f " + path);
    }

    @Override
    public String kind() {
        return clusterResource.kind();
    }

    @Override
    public String name() {
        return clusterResource.name();
    }

    @Override
    public void delete(ClusterCLI cli) {
        clusterResource.delete(cli);
    }

    private String getRemoteHash(ClusterCLI cli) {
        List<String> output = cli.execute("oc get " + clusterResource.kind() + " " + clusterResource.name() + " -o custom-columns=\"hash:.metadata.labels.configHash\"")
                .getOutputLines();
        if (output.get(0).toLowerCase().contains("not found")) return "noHashFound";
        return output.get(1).strip();
    }

    private String getLocalHash() {
        return getString(loadFromPath(path), "metadata", "labels", "configHash");
    }

    @Override
    public int compareTo(LocalClusterResource other) {
        return path.compareTo(other.path());
    }
}
