package io.microconfig.osdf.chaos.metrics;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Map.Entry;

import static io.microconfig.osdf.utils.YamlUtils.getDouble;
import static io.microconfig.osdf.utils.YamlUtils.getString;

@ToString
@RequiredArgsConstructor
public class Metric {
    private final String name;
    private final String tag;

    private final Double referenceValue;

    private final Double upperBound;
    private final Double lowerBound;

    @SuppressWarnings("unchecked")
    public static Metric fromEntry(Entry<String, Object> entry, Map<String, Double> refValues) {
        Map<String, Object> metricMap = (Map<String, Object>) entry.getValue();
        String tag = getString(metricMap, "tag");
        if (!refValues.containsKey(tag)) throw new OSDFException("No such metric found [" + tag + "]");
        Double referenceValue = refValues.get(tag);
        Double upperBound = getDouble(metricMap, "max") != null ? getDouble(metricMap, "max") : Double.MAX_VALUE;
        Double lowerBound = getDouble(metricMap, "min") != null ? getDouble(metricMap, "min") : -Double.MAX_VALUE;
        return new Metric(entry.getKey(), tag, referenceValue, upperBound, lowerBound);
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
