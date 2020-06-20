package io.microconfig.osdf.loadtesting.jmeter.results;

import io.microconfig.osdf.loadtesting.jmeter.metrics.PeakMetrics;
import io.microconfig.osdf.metrics.Metric;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.metrics.MetricUtils.getMetricValueByContainsTag;

@RequiredArgsConstructor
public class PeakResultsBuilder {
    private final Set<Metric> metricsFromConfig;
    private final PeakMetrics currentPeakMetrics;

    public static PeakResultsBuilder peakResultsBuilder(Set<Metric> metricsFromConfig, PeakMetrics currentPeakMetrics) {
        return new PeakResultsBuilder(metricsFromConfig, currentPeakMetrics);
    }

    public String build() {
        DecimalFormat df = new DecimalFormat("###.##");
        StringBuilder builder = new StringBuilder("This step results: ");
        builder.append(" [ Errors: ").append(df.format(currentPeakMetrics.getErrors())).append("%] ");
        builder.append(" [ Average: ").append(df.format(currentPeakMetrics.getResponseTime())).append(" ms] ");
        builder.append(" [ Load: ").append(df.format(currentPeakMetrics.getLoadRequests())).append(" req/sec] ");
        buildUserMetrics(builder, df);
        return builder.toString();
    }

    private void buildUserMetrics(StringBuilder builder, DecimalFormat df) {
        metricsFromConfig.forEach(metric -> builder.append(" [ ").append(metric.getName()).append(": ")
                .append(df.format(getCurrentValue(metric, currentPeakMetrics.getPulledMetrics()))).append(" ]"));
    }

    private Double getCurrentValue(Metric metric, Map<String, Double> pulledMetrics) {
        return metric.getContainsTag() == null ? pulledMetrics.get(metric.getTag()) :
                                getMetricValueByContainsTag(pulledMetrics, metric.getContainsTag());
    }
}
