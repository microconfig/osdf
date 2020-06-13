package io.microconfig.osdf.chaos.metrics;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.MetricsPuller;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.error;
import static java.lang.Math.min;
import static java.time.Duration.between;
import static java.time.LocalTime.now;

@RequiredArgsConstructor
public class MetricsChecker {

    private final Long duration;
    private final Integer timeout;
    private final MetricsPuller puller;
    private final Set<Metric> metricsSet;

    public static MetricsChecker metricsChecker(Long duration, Integer timeout, MetricsPuller puller, Set<Metric> metricSet) {
        return new MetricsChecker(duration, timeout, puller, metricSet);
    }

    public void run() {
        LocalTime end = now().plusSeconds(duration);
        while (now().isBefore(end)) {
            sleepSec(min(timeout, between(now(), end).toSeconds()));
            try {
                checkMetrics();
            } catch (Exception e) {
                error(e.getMessage());
                throw new OSDFException("Metrics check failed", e);
            }
        }
    }

    private void checkMetrics() throws OSDFException {
        if (!metricsSet.isEmpty()) {
            Map<String, Double> metrics = puller.pull();
            metricsSet.forEach(metric -> metric.checkMetric(metrics));
        }
    }
}
