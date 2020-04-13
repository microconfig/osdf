package io.microconfig.osdf.metrics;

import io.microconfig.osdf.metrics.formats.MetricsParser;
import io.microconfig.osdf.utils.HTTPUtils;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MetricsPuller {
    private final MetricsParser metricsParser;
    private final String url;

    public static MetricsPuller metricsPuller(MetricsParser format, String url) {
        return new MetricsPuller(format, url);
    }

    public Map<String, Double> pull() {
        return metricsParser.get(HTTPUtils.get(url));
    }
}
