package io.microconfig.osdf.metrics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.utils.Logger.warn;

@RequiredArgsConstructor
public class Metric {
    @Getter
    private final String name;
    private final String tag;

    private final Double upperBound;
    private final Double lowerBound;

    public static Metric metric(String name, String tag, Double upperBound, Double lowerBound) {
        return new Metric(name, tag, upperBound, lowerBound);
    }

    public boolean checkMetric(Map<String, Double> metricsMap) {
        Double currentValue = metricsMap.get(tag);
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
