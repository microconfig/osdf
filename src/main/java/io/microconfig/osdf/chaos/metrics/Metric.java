package io.microconfig.osdf.chaos.metrics;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Map.Entry;

import static io.microconfig.osdf.utils.YamlUtils.getDouble;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static java.lang.Double.*;

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
        Double upperBound = calcUpperBound(metricMap, referenceValue);
        Double lowerBound = calcLowerBound(metricMap, referenceValue);
        return new Metric(entry.getKey(), tag, referenceValue, upperBound, lowerBound);
    }

    private static Double calcLowerBound(Map<String, Object> metricMap, Double referenceValue) {
        double baseline = metricMap.containsKey("baseline") ? parseDouble(getString(metricMap, "baseline")) : referenceValue;
        double lowerBound = getDouble(metricMap, "min") != null ? getDouble(metricMap, "min") : -MAX_VALUE;
        if (metricMap.containsKey("deviation")) {
            double absDeviation = calcAbsDeviation(getString(metricMap, "deviation"), baseline);
            lowerBound = max(lowerBound, baseline - absDeviation);
        }
        return lowerBound;
    }

    private static Double calcUpperBound(Map<String, Object> metricMap, Double referenceValue) {

        double baseline = metricMap.containsKey("baseline") ? parseDouble(getString(metricMap, "baseline")) : referenceValue;
        double upperBound = getDouble(metricMap, "max") != null ? getDouble(metricMap, "max") : MAX_VALUE;
        if (metricMap.containsKey("deviation")) {
            double absDeviation = calcAbsDeviation(getString(metricMap, "deviation"), baseline);
            upperBound = min(upperBound, baseline + absDeviation);
        }
        return upperBound;
    }

    private static double calcAbsDeviation(String deviationStr, double baseline) {
        double absDeviation;
        if (deviationStr.contains("%")) {
            absDeviation = baseline * parseDouble(deviationStr.substring(0, deviationStr.length() - 1)) / 100.0;
        } else {
            absDeviation = parseDouble(deviationStr);
        }
        return absDeviation;
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
