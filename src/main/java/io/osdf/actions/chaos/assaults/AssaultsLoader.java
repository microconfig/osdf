package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.events.EventStorage;
import io.osdf.actions.chaos.state.ChaosStateManager;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static io.osdf.actions.chaos.assaults.ComponentsAssault.componentsAssault;
import static io.osdf.actions.chaos.assaults.DeleteResourceAssault.deleteResourceAssault;
import static io.osdf.actions.chaos.assaults.DeployAssault.deployAssault;
import static io.osdf.actions.chaos.assaults.PodsAssault.podsAssault;
import static java.util.Map.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class AssaultsLoader {
    private final ChaosContext chaosContext;

    public static AssaultsLoader assaultsLoader(ChaosContext chaosContext) {
        return new AssaultsLoader(chaosContext);
    }

    public List<Assault> load(Map<String, Object> description) {
        return assaults().entrySet().stream()
                .filter(entry -> description.containsKey(entry.getKey()))
                .map(entry -> entry.getValue().apply(description.get(entry.getKey()), chaosContext))
                .collect(toUnmodifiableList());
    }

    private Map<String, BiFunction<Object, ChaosContext, Assault>> assaults() {
        return of(
                "components", ComponentsAssault::componentsAssault,
                "killPods", PodsAssault::podsAssault,
                "deleteResource", DeleteResourceAssault::deleteResourceAssault,
                "deploy", DeployAssault::deployAssault
        );
    }
}
