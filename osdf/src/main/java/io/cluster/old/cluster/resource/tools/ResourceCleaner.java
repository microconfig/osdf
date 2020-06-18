package io.cluster.old.cluster.resource.tools;

import io.cluster.old.cluster.cli.ClusterCli;
import io.cluster.old.cluster.resource.ClusterResource;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.List.of;
import static java.util.function.Predicate.not;

@RequiredArgsConstructor
public class ResourceCleaner {
    private static final List<String> SYSTEM_RESOURCES = of("pod", "replicationcontroller");

    private final ClusterCli cli;

    public static ResourceCleaner resourceCleaner(ClusterCli cli) {
        return new ResourceCleaner(cli);
    }

    public void cleanOld(List<? extends ClusterResource> localResources, List<? extends ClusterResource> remoteResources) {
        remoteResources.stream()
                .filter(not(localResources::contains))
                .filter(resource -> !SYSTEM_RESOURCES.contains(resource.kind().toLowerCase()))
                .forEach(resource -> resource.delete(cli));
    }
}
