package io.osdf.actions.chaos.assaults;

import io.osdf.actions.chaos.ChaosContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

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
