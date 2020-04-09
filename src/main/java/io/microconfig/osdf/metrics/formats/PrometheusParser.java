package io.microconfig.osdf.metrics.formats;

import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.String.join;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Stream.of;

public class PrometheusParser implements MetricsParser {
    public static PrometheusParser prometheusParser() {
        return new PrometheusParser();
    }

    @Override
    public Map<String, Double> get(String rawMetrics) {
        return of(rawMetrics.split("\n"))
                .filter(line -> !line.startsWith("#"))
                .map(line -> line.split(" "))
                .collect(toUnmodifiableMap(
                        split -> join(" ", copyOfRange(split, 0, split.length - 1)),
                        split -> parseDouble(split[split.length - 1])
                ));
    }
}
