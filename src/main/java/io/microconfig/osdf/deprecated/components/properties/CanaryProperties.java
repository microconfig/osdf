package io.microconfig.osdf.deprecated.components.properties;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.Metric;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.metrics.Metric.metric;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
@Getter
public class CanaryProperties {
    private final String url;
    private final int intervalInSec;
    private final int step;
    private final List<Metric> metrics;

    public static CanaryProperties canaryProperties(Path componentPath) {
        Map<String, Object> yaml = loadFromPath(of(componentPath + "/deploy.yaml"));
        Map<String, Object> canary = getMap(yaml, "canary");
        if (canary == null) throw new OSDFException("Canary config not found");

        int intervalInSec = getInt(canary, "deploy", "intervalInSec");
        int step = getInt(canary, "deploy", "step");
        String url = getString(canary, "validation", "url");
        return new CanaryProperties(url, intervalInSec, step, metrics(canary));
    }

    @SuppressWarnings("unchecked")
    private static List<Metric> metrics(Map<String, Object> canary) {
        List<Object> list = getList(canary, "validation", "metrics");
        if (list == null) return List.of();
        return list.stream()
                .map(obj -> (Map<String, Object>) obj)
                .map(m -> metric(getString(m, "key"), getString(m, "deviation")))
                .collect(toUnmodifiableList());
    }
}
