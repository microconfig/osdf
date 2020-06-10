package io.microconfig.osdf.service.deployment.common;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.resource.ClusterResource;
import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import io.microconfig.osdf.service.ClusterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class CommonClusterService implements ClusterService {
    private final String name;
    private final String version;
    private final List<LocalClusterResource> resourcesNames;
    private final ClusterCLI cli;

    public static CommonClusterService outerClusterService(String name,
                                                           String version,
                                                           ClusterCLI cli,
                                                           List<LocalClusterResource> resourcesNames) {
        return new CommonClusterService(name, version, resourcesNames, cli);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public List<ClusterResource> resources() {
        return resourcesNames.stream().map(s -> (ClusterResource) s).collect(toUnmodifiableList());
    }

    @Override
    public void upload(List<LocalClusterResource> resources) {
        resources.forEach(resource -> resource.upload(cli));
    }
}
