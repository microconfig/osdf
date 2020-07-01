package unstable.io.osdf.loadtesting.metrics;

import unstable.io.osdf.metrics.Metric;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class PeakMetrics {
    private final Set<Metric> metricsFromConfig;
    private int step;
    private double errors;
    private double responseTime;
    private double loadRequests;
    private Map<String, Double> pulledMetrics;

    public static PeakMetrics peakMetrics(Set<Metric> metricsFromConfig) {
        return new PeakMetrics(metricsFromConfig);
    }

    public boolean checkPeakResults(PeakMetrics currentPeakMetrics) {
        if (checkErrors(currentPeakMetrics)) return true;
        if (checkResponseTime(currentPeakMetrics)) return true;
        if (currentPeakMetrics.getPulledMetrics() != null && !currentPeakMetrics.getPulledMetrics().isEmpty())
            return metricsFromConfig.stream().anyMatch(metric -> !metric.checkMetric(currentPeakMetrics.getPulledMetrics()));
        return false;
    }

    private boolean checkResponseTime(PeakMetrics currentPeakMetrics) {
        return responseTime != 0
                && currentPeakMetrics.getResponseTime() != 0
                && responseTime < currentPeakMetrics.getResponseTime();
    }

    private boolean checkErrors(PeakMetrics currentPeakMetrics) {
        return errors != 0 && currentPeakMetrics.getErrors() != 0 && errors < currentPeakMetrics.getErrors();
    }
}
