package io.cluster.old.cluster.resource;

import io.cluster.old.cluster.cli.ClusterCli;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.cluster.old.cluster.resource.ClusterResourceImpl.fromPath;

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
    public void upload(ClusterCli cli) {
        cli.execute("oc apply -f " + path).throwExceptionIfError();
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
}
