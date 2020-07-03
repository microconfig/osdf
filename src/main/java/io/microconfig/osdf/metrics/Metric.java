package io.microconfig.osdf.metrics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.metrics.MetricUtils.getMetricValueByContainsTag;
import static io.microconfig.utils.Logger.warn;

@Getter
@RequiredArgsConstructor
public class Metric {
    private final String name;
    private final String tag;
    private final String containsTag;

    private final Double upperBound;
    private final Double lowerBound;

    public static Metric metricByTag(String name, String tag, Double upperBound, Double lowerBound) {
        return new Metric(name, tag, null, upperBound, lowerBound);
    }

    public static Metric metricContainsTag(String name, String containsTag, Double upperBound, Double lowerBound) {
        return new Metric(name, null, containsTag, upperBound, lowerBound);
    }

    public boolean checkMetric(Map<String, Double> metricsMap) {
        Double currentValue = containsTag == null ? metricsMap.get(tag) : getMetricValueByContainsTag(metricsMap, containsTag);
        if (currentValue > upperBound) {
            warn("Metric " + name + " [" + tag + "] value [" + currentValue + "] exceeded the specified max value " + upperBound);
            return false;
        }
        if (currentValue < lowerBound) {
            warn("Metric " + name + " [" + tag + "] value [" + currentValue + "] dropped below the specified min value " + lowerBound);
            return false;
        }
        return true;
    }
}
