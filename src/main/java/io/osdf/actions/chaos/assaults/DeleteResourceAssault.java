package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.events.EventSender;
import io.osdf.actions.chaos.state.AssaultInfoManager;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.resource.LocalClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.osdf.actions.chaos.events.EventLevel.CHAOS;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.appLoader;
import static io.osdf.core.application.core.files.loaders.filters.RequiredComponentsFilter.requiredComponentsFilter;
import static java.util.List.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DeleteResourceAssault implements Assault {
    private final List<LocalClusterResource> resources;
    private final ClusterCli cli;

    private final AssaultInfoManager assaultInfoManager;
    private final EventSender events;

    @SuppressWarnings("unchecked")
    public static DeleteResourceAssault deleteResourceAssault(Object description, ChaosContext chaosContext) {
        List<LocalClusterResource> resources = ((List<Map<String, String>>) description).stream()
                .map(resourceDescription -> {
                    String componentName = resourceDescription.get("component");
                    String kind = resourceDescription.get("kind");
                    String name = resourceDescription.get("name");
                    List<ApplicationFiles> components = appLoader(chaosContext.paths()).withDirFilter(requiredComponentsFilter(of(componentName))).load();
                    if (components.isEmpty()) throw new RuntimeException("Unknown component " + componentName);
                    return components.get(0).resources().stream()
                            .filter(resource -> resource.name().equalsIgnoreCase(name))
                            .filter(resource -> resource.kind().equalsIgnoreCase(kind))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Resource " + kind + "/" + name + " not found"));
                })
                .collect(toUnmodifiableList());
        return new DeleteResourceAssault(
                resources,
                chaosContext.cli(),
                chaosContext.chaosStateManager().assaultInfoManager(),
                chaosContext.eventStorage().sender("delete-resource")
        );
    }

    @Override
    public void start() {
        resources.forEach(resource -> resource.delete(cli));
        String names = resources.stream().map(resource -> resource.kind() + "/" + resource.name()).collect(joining(","));

        events.send("Deleted resources: " + names, CHAOS);
        assaultInfoManager.save(new ActiveAssaultInfo(
                "delete-resource",
                "Deleted resources: " + names,
                Map.of("resources", names)
        ));
    }

    @Override
    public void stop() {
        resources.forEach(resource -> resource.upload(cli));
        String names = resources.stream().map(resource -> resource.kind() + "/" + resource.name()).collect(joining(","));

        events.send("Uploaded resources: " + names, CHAOS);
        assaultInfoManager.delete("delete-resource");
    }

    @Override
    public void clear() {
        ActiveAssaultInfo assaultInfo = assaultInfoManager.get("delete-resource");
        if (assaultInfo == null) return;
        stop();
    }
}
