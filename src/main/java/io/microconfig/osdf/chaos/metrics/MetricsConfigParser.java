package io.microconfig.osdf.chaos.metrics;

import io.microconfig.osdf.metrics.MetricsPuller;
import io.microconfig.osdf.metrics.formats.PrometheusParser;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.utils.Logger.warn;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class MetricsConfigParser {

    public static MetricsConfigParser metricsConfigParser() {
        return new MetricsConfigParser();
    }

    @SuppressWarnings("unchecked")
    public Set<Metric> fromYaml(Object o) {
        Map<String, Object> metricsMap = getMap((Map<String, Object>) o, "monitoring", "metrics");
        if (metricsMap == null || metricsMap.isEmpty()) {
            warn("no metrics for monitoring specified");
            return emptySet();
        }
        Map<String, Double> refValues = buildPuller(o).pull();
        return metricsMap.entrySet().stream().map(entry -> Metric.fromEntry(entry, refValues)).collect(toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public MetricsPuller buildPuller(Object o) {
        String uri = getString((Map<String, Object>) o, "monitoring", "metricsURI");
        return MetricsPuller.metricsPuller(PrometheusParser.prometheusParser(), uri);
    }
}
