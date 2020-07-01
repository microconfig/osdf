package unstable.io.osdf.istio.rules;

import io.osdf.common.exceptions.OSDFException;
import unstable.io.osdf.istio.Destination;
import unstable.io.osdf.istio.Fault;
import unstable.io.osdf.istio.WeightRoute;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static unstable.io.osdf.istio.Destination.destination;
import static io.osdf.common.utils.YamlUtils.getList;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@AllArgsConstructor
public class MainRule {
    private static final String FAULT_TAG = "fault";
    private static final String MIRROR_TAG = "mirror";

    private List<WeightRoute> routes;
    private Destination mirror;
    private Fault fault;

    @SuppressWarnings("unchecked")
    public static MainRule fromYaml(Object ruleObject) {
        Map<String, Object> rule = (Map<String, Object>) ruleObject;

        Destination mirror = null;
        Fault fault = null;
        if (rule.containsKey(MIRROR_TAG)) {
            mirror = Destination.fromYaml(rule.get(MIRROR_TAG));
        }
        if (rule.containsKey(FAULT_TAG)) {
            fault = Fault.fromYaml(rule.get(FAULT_TAG));
        }
        List<WeightRoute> routes = getList(rule, "route")
                .stream()
                .map(WeightRoute::fromYaml)
                .collect(toUnmodifiableList());
        return new MainRule(routes, mirror, fault);
    }

    public void setWeight(String subset, int weight) {
        WeightRoute otherRoute = getOtherRoute(subset);
        if (otherRoute == null) throw new OSDFException("No other versions found");

        Destination destination = destination(otherRoute.getDestination().getHost(), subset);
        routes = List.of(
                WeightRoute.weightRoute(otherRoute.getDestination(), 100 - weight),
                WeightRoute.weightRoute(destination, weight)
        ).stream()
                .filter(r -> r.getWeight() != 0)
                .collect(toUnmodifiableList());

        deleteMirrorIfExists(subset);
    }

    public void setMirror(String subset) {
        List<WeightRoute> otherRoutes = getOtherRoutes(subset);
        if (otherRoutes.isEmpty()) throw new OSDFException("No other versions found");

        setRoutesAndNormalize(otherRoutes);
        mirror = destination(otherRoutes.get(0).getDestination().getHost(), subset);
    }

    public void deleteSubset(String subset) {
        deleteMirrorIfExists(subset);
        setRoutesAndNormalize(getOtherRoutes(subset));
    }

    public int getWeight(String subset) {
        return routes.stream()
                .filter(route -> route.getDestination().getSubset().equals(subset))
                .findFirst()
                .map(WeightRoute::getWeight)
                .orElse(0);
    }

    public String mirrorSubset() {
        return mirror == null ? null : mirror.getSubset();
    }

    public boolean isEmpty() {
        return routes.isEmpty();
    }

    private void setRoutesAndNormalize(List<WeightRoute> otherRoutes) {
        routes = otherRoutes.size() == 1 ? List.of(WeightRoute.weightRoute(otherRoutes.get(0).getDestination(), 100)) : otherRoutes;
    }

    private void deleteMirrorIfExists(String subset) {
        if (mirror != null && mirror.getSubset().equals(subset)) {
            mirror = null;
        }
    }

    private WeightRoute getOtherRoute(String subset) {
        List<WeightRoute> otherRoutes = getOtherRoutes(subset);
        if (otherRoutes.isEmpty()) return null;
        if (otherRoutes.size() != 1) throw new OSDFException("Only one other version must exist");
        return otherRoutes.get(0);
    }

    private List<WeightRoute> getOtherRoutes(String subset) {
        return routes
                .stream()
                .filter(r -> !r.getDestination().getSubset().equals(subset))
                .collect(toUnmodifiableList());
    }

    public void setFault(Fault fault) {
        this.fault = fault;
    }

    public Object toYaml() {
        Map<String, Object> route = new HashMap<>();

        route.put("route", routes.stream().map(WeightRoute::toYaml).collect(toUnmodifiableList()));
        if (mirror != null) {
            route.put(MIRROR_TAG, mirror.toYaml());
        }
        if (fault != null) {
            route.put(FAULT_TAG, fault.toYaml());
        }
        return route;
    }
}
