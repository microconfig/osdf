package io.microconfig.osdf.metrics;

import java.util.Map;

public class MetricUtils {
    private MetricUtils() { }

    public static double getMetricValueByContainsTag(Map<String, Double> metricsMap, String containsTag) {
        return metricsMap.keySet()
                .stream()
                .filter(key -> key.contains(containsTag))
                .mapToDouble(metricsMap::get)
                .sum();
    }
}
