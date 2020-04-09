package io.microconfig.osdf.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static io.microconfig.osdf.metrics.MetricType.ABSOLUTE;
import static io.microconfig.osdf.metrics.MetricType.RELATIVE;
import static java.lang.Double.parseDouble;

@AllArgsConstructor
@Getter
public class Metric {
    private final String key;
    private final MetricType type;
    private final double deviation;

    public static Metric metric(String key, String deviationStr) {
        MetricType type = deviationStr.contains("%") ? RELATIVE : ABSOLUTE;
        double deviation = parseDouble(deviationStr.contains("%") ? deviationStr.substring(0, deviationStr.length() - 1) : deviationStr);
        return new Metric(key, type, deviation);
    }
}
