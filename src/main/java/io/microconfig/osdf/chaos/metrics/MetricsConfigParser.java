package io.microconfig.osdf.chaos.metrics;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.MetricsPuller;
import io.microconfig.osdf.metrics.formats.PrometheusParser;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.chaos.metrics.Metric.metric;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static io.microconfig.utils.Logger.warn;
import static java.lang.Double.*;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class MetricsConfigParser {

    public static final String BASELINE = "baseline";
    public static final String DEVIATION = "deviation";

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
        return metricsMap.entrySet().stream().map(entry -> fromEntry(entry, refValues)).collect(toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public Metric fromEntry(Map.Entry<String, Object> entry, Map<String, Double> refValues) {
        Map<String, Object> metricMap = (Map<String, Object>) entry.getValue();
        String tag = getString(metricMap, "tag");
        if (!refValues.containsKey(tag)) throw new OSDFException("No such metric found [" + tag + "]");
        double referenceValue = metricMap.containsKey(BASELINE) ? parseDouble(getString(metricMap, BASELINE)) : refValues.get(tag);
        Double upperBound = calcUpperBound(metricMap, referenceValue);
        Double lowerBound = calcLowerBound(metricMap, referenceValue);
        return metric(entry.getKey(), tag, upperBound, lowerBound);
    }

    private Double calcLowerBound(Map<String, Object> metricMap, Double baseline) {
        double lowerBound = getDouble(metricMap, "min") != null ? getDouble(metricMap, "min") : -MAX_VALUE;
        if (metricMap.containsKey(DEVIATION)) {
            double absDeviation = calcAbsDeviation(getString(metricMap, DEVIATION), baseline);
            lowerBound = max(lowerBound, baseline - absDeviation);
        }
        return lowerBound;
    }

    private Double calcUpperBound(Map<String, Object> metricMap, Double baseline) {
        double upperBound = getDouble(metricMap, "max") != null ? getDouble(metricMap, "max") : MAX_VALUE;
        if (metricMap.containsKey(DEVIATION)) {
            double absDeviation = calcAbsDeviation(getString(metricMap, DEVIATION), baseline);
            upperBound = min(upperBound, baseline + absDeviation);
        }
        return upperBound;
    }

    private double calcAbsDeviation(String deviationStr, double baseline) {
        double absDeviation;
        if (deviationStr.contains("%")) {
            absDeviation = baseline * parseDouble(deviationStr.substring(0, deviationStr.length() - 1)) / 100.0;
        } else {
            absDeviation = parseDouble(deviationStr);
        }
        return absDeviation;
    }

    @SuppressWarnings("unchecked")
    public MetricsPuller buildPuller(Object o) {
        String url = getString((Map<String, Object>) o, "monitoring", "metricsURL");
        return MetricsPuller.metricsPuller(PrometheusParser.prometheusParser(), url);
    }
}
