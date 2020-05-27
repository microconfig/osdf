package io.microconfig.osdf.cluster.resource.tools;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.resource.ClusterResource;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
public class ResourceCleaner {
    private final ClusterCLI cli;

    public static ResourceCleaner resourceCleaner(ClusterCLI cli) {
        return new ResourceCleaner(cli);
    }

    public void cleanOld(List<? extends ClusterResource> localResources, List<? extends ClusterResource> remoteResources) {
        remoteResources.stream()
                .filter(not(localResources::contains))
                .forEach(resource -> resource.delete(cli));
    }
}
