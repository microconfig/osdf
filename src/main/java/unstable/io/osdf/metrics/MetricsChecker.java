package unstable.io.osdf.metrics;

import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static io.osdf.common.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.warn;
import static java.lang.Math.min;
import static java.time.Duration.between;
import static java.time.LocalTime.now;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class MetricsChecker {

    private final Long duration;
    private final Integer timeout;
    private final MetricsPuller puller;
    private final Set<Metric> metricsSet;

    public static MetricsChecker metricsChecker(Long duration, Integer timeout, MetricsPuller puller, Set<Metric> metricSet) {
        return new MetricsChecker(duration, timeout, puller, metricSet);
    }

    public boolean runCheck() {
        LocalTime end = now().plusSeconds(duration);
        while (now().isBefore(end)) {
            sleepSec(min(timeout, between(now(), end).toSeconds()));
            if (!check()) return false;
        }
        return true;
    }

    private boolean check() {
        if (metricsSet.isEmpty()) return true;
        Map<String, Double> metrics = puller.pull();
        Set<Metric> failedMetrics = metricsSet.stream()
                .filter(metric -> !metric.checkMetric(metrics))
                .collect(toUnmodifiableSet());

        if (!failedMetrics.isEmpty()) {
            String failedMetricsNames = failedMetrics.stream().map(Metric::getName).collect(joining(", "));
            warn("Metrics check failed: " + failedMetricsNames);
            return false;
        }
        return true;
    }
}
