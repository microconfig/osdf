package io.microconfig.osdf.istio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static java.util.Map.of;

@AllArgsConstructor
@Getter
public class WeightRoute {
    @NonNull
    private final Destination destination;
    private int weight;

    public static WeightRoute weightRoute(Destination destination, int weight) {
        return new WeightRoute(destination, weight);
    }

    @SuppressWarnings("unchecked")
    public static WeightRoute fromYaml(Object routeObj) {
        Map<String, Object> route = (Map<String, Object>) routeObj;

        int weight = getInt(route, "weight");
        Destination destination = Destination.fromYaml(getMap(route, "destination"));

        return new WeightRoute(destination, weight);
    }

    public Object toYaml() {
        return of(
                "destination", destination.toYaml(),
                "weight", weight
        );
    }

}
