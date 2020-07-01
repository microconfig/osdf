package io.osdf.core.cluster.resource;

import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Objects;

import static io.osdf.core.cluster.resource.ClusterResourceImpl.fromPath;
import static io.microconfig.utils.Logger.info;
import static java.util.Objects.hash;

@RequiredArgsConstructor
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
    public void upload(ClusterCli cli) {
        String output = cli.execute("apply -f " + path).getOutput();
        if (output.contains("field is immutable")) {
            info("Immutable field changed in " + clusterResource.kind() + " " + clusterResource.name());
            cli.execute("delete " + clusterResource.kind() + " " + clusterResource.name());
            cli.execute("apply -f " + path).throwExceptionIfError();
        }
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
    public String label(ClusterCli cli, String key) {
        return clusterResource.label(cli, key);
    }

    @Override
    public void delete(ClusterCli cli) {
        clusterResource.delete(cli);
    }

    @Override
    public int compareTo(LocalClusterResource other) {
        return path.compareTo(other.path());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClusterResource)) return false;
        ClusterResource that = (ClusterResource) o;
        return Objects.equals(clusterResource.kind(), that.kind()) && Objects.equals(clusterResource.name(), that.name());
    }

    @Override
    public int hashCode() {
        return hash(clusterResource);
    }
}
