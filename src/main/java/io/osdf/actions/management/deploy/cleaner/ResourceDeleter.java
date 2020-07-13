package io.osdf.actions.management.deploy.cleaner;

import io.osdf.core.application.CoreDescription;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.List.of;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class ResourceDeleter {
    private static final List<String> SYSTEM_RESOURCES = of("pod", "replicationcontroller");

    private final ClusterCli cli;

    public static ResourceDeleter resourceDeleter(ClusterCli cli) {
        return new ResourceDeleter(cli);
    }

    public ResourceDeleter deleteOldResources(CoreDescription description, ApplicationFiles files) {
        List<? extends ClusterResource> remote = description.getResources().stream()
                .map(ClusterResourceImpl::fromOpenShiftNotation)
                .collect(toUnmodifiableList());
        List<? extends ClusterResource> local = files.resources();

        remote.stream()
                .filter(not(local::contains))
                .filter(resource -> !SYSTEM_RESOURCES.contains(resource.kind().toLowerCase()))
                .forEach(resource -> resource.delete(cli));
        return this;
    }
    
    public void deleteConfigMaps(CoreDescription description) {
        description.getResources().stream()
                .map(ClusterResourceImpl::fromOpenShiftNotation)
                .filter(resource -> resource.kind().equals("configmap"))
                .forEach(resource -> resource.delete(cli));
    }
}