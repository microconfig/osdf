package unstable.io.osdf.metrics;

import io.osdf.common.exceptions.OSDFException;
import unstable.io.osdf.metrics.formats.PrometheusParser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static unstable.io.osdf.metrics.Metric.*;
import static unstable.io.osdf.metrics.MetricsPuller.metricsPuller;
import static io.osdf.common.utils.YamlUtils.*;
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
        List<Map<String, Object>> metricsList = getListOfMaps((Map<String, Object>) o, "metrics");
        if (metricsList == null || metricsList.isEmpty()) {
            warn("No metrics for monitoring specified");
            return emptySet();
        }
        Map<String, Double> refValues = buildPuller(o).pull();
        return metricsList.stream().map(map -> fromEntry(map, refValues)).collect(toUnmodifiableSet());
    }

    private Metric fromEntry(Map<String, Object> metricMap, Map<String, Double> refValues) {
        String metricName = metricMap.keySet()
                .stream()
                .findFirst()
                .orElseThrow();
        Map<String, Object> metricParams = getMap(metricMap, metricName);
        String tag = getString(metricParams, "tag");
        if (!tag.equals("null")) return getMetricByTag(metricName, refValues, metricParams, tag);
        String containsTag = getString(metricParams, "contains_tag");
        if (!containsTag.equals("null")) return getMetricByContainsTag(metricName, refValues, metricParams, containsTag);
        throw new OSDFException("Please define 'tag' or 'contains_tag' in monitoring config");

    }

    private Metric getMetricByTag(String metricName, Map<String, Double> refValues, Map<String, Object> metricParams, String tag) {
        if (!refValues.containsKey(tag)) throw new OSDFException("No such metric found [" + tag + "]");
        double referenceValue = metricParams.containsKey(BASELINE) ? parseDouble(getString(metricParams, BASELINE)) : refValues.get(tag);
        Double upperBound = calcUpperBound(metricParams, referenceValue);
        Double lowerBound = calcLowerBound(metricParams, referenceValue);
        return metricByTag(metricName, tag, upperBound, lowerBound);
    }

    private Metric getMetricByContainsTag(String metricName, Map<String, Double> refValues,
                                          Map<String, Object> metricParams, String containsTag) {
        double referenceValue = metricParams.containsKey(BASELINE) ?
                parseDouble(getString(metricParams, BASELINE)) :
                MetricUtils.getMetricValueByContainsTag(refValues, containsTag);
        Double upperBound = calcUpperBound(metricParams, referenceValue);
        Double lowerBound = calcLowerBound(metricParams, referenceValue);
        return metricContainsTag(metricName, containsTag, upperBound, lowerBound);
    }

    private Double calcLowerBound(Map<String, Object> metricParams, Double baseline) {
        double lowerBound = getDouble(metricParams, "min") != null ? getDouble(metricParams, "min") : -MAX_VALUE;
        if (metricParams.containsKey(DEVIATION)) {
            double absDeviation = calcAbsDeviation(getString(metricParams, DEVIATION), baseline);
            lowerBound = max(lowerBound, baseline - absDeviation);
        }
        return lowerBound;
    }

    private Double calcUpperBound(Map<String, Object> metricParams, Double baseline) {
        double upperBound = getDouble(metricParams, "max") != null ? getDouble(metricParams, "max") : MAX_VALUE;
        if (metricParams.containsKey(DEVIATION)) {
            double absDeviation = calcAbsDeviation(getString(metricParams, DEVIATION), baseline);
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
        String url = getString((Map<String, Object>) o, "url");
        return metricsPuller(PrometheusParser.prometheusParser(), url);
    }
}
