package io.microconfig.osdf.metrics.formats;

import java.util.Map;

public interface MetricsParser {
    Map<String, Double> get(String rawMetrics);
}
