package io.microconfig.osdf.deprecated.components.properties;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.Metric;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.metrics.MetricsConfigParser.metricsConfigParser;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
@Getter
public class CanaryProperties {
    private final String url;
    private final int intervalInSec;
    private final int step;
    private final Set<Metric> metrics;

    public static CanaryProperties canaryProperties(Path componentPath) {
        Map<String, Object> yaml = loadFromPath(of(componentPath + "/deploy.yaml"));
        Map<String, Object> canary = getMap(yaml, "canary");
        if (canary == null) throw new OSDFException("Canary config not found");

        int intervalInSec = getInt(canary, "deploy", "intervalInSec");
        int step = getInt(canary, "deploy", "step");
        String url = getString(canary, "validation", "url");
        return new CanaryProperties(url, intervalInSec, step, metrics(canary));
    }

    private static Set<Metric> metrics(Map<String, Object> canary) {
        return metricsConfigParser().fromYaml(getMap(canary, "validation"));
    }
}
