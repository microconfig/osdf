package io.microconfig.osdf.chaos.metrics;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@RequiredArgsConstructor
public class Metric {
    private final String name;
    private final String tag;

    private final Double upperBound;
    private final Double lowerBound;

    public static Metric metric(String key, String tag, Double upperBound, Double lowerBound) {
        return new Metric(key, tag, upperBound, lowerBound);
    }

    public void checkMetric(Map<String, Double> values) {
        Double currentValue = values.get(tag);
        if (currentValue > upperBound) {
            throw new OSDFException("Metric " + name + " [" + tag + "] value [" + currentValue + "] exceeded the specified max value " + upperBound);
        }
        if (currentValue < lowerBound) {
            throw new OSDFException("Metric " + name + " [" + tag + "] value [" + currentValue + "] dropped below the specified min value " + lowerBound);
        }
    }
}
