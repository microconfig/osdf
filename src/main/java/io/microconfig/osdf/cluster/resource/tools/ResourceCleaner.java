package io.microconfig.osdf.cluster.resource.tools;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.resource.ClusterResource;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.utils.Logger.info;
import static java.util.List.of;
import static java.util.function.Predicate.not;

@RequiredArgsConstructor
public class ResourceCleaner {
    private static final List<String> SYSTEM_RESOURCES = of("pod", "replicationcontroller", "configmap");

    private final ClusterCLI cli;

    public static ResourceCleaner resourceCleaner(ClusterCLI cli) {
        return new ResourceCleaner(cli);
    }

    public void cleanOld(List<? extends ClusterResource> localResources, List<? extends ClusterResource> remoteResources) {
        remoteResources.stream()
                .filter(not(localResources::contains))
                .filter(resource -> !SYSTEM_RESOURCES.contains(resource.kind().toLowerCase()))
                .peek(resource -> info("Deleting " + resource.kind() + " " + resource.name()))
                .forEach(resource -> resource.delete(cli));
    }
}
