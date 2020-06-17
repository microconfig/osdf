package io.microconfig.osdf.loadtesting.jmeter.metrics.loader;

import io.microconfig.osdf.exceptions.ServerNotAvailableException;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.utils.ThreadUtils.sleepMs;
import static io.microconfig.utils.Logger.announce;

@Data
@RequiredArgsConstructor
public class MetricsPuller implements Runnable {
    private final List<Map<String, Double>> metrics;
    private final PullerSettings settings;

    public static MetricsPuller metricsPuller(List<Map<String, Double>> metrics, PullerSettings settings) {
        return new MetricsPuller(metrics, settings);
    }

    @Override
    public void run() {
        try {
            int timesToLoad = settings.getTimesToLoad();
            while (timesToLoad != 0) {
                sleepMs(settings.getDelay());
                metrics.add(settings.getPuller().pull());
                timesToLoad--;
            }
        } catch (ServerNotAvailableException e) {
            announce("Server not available while getting metrics.");
        }
        finally {
            settings.getCountDownLatch().countDown();
        }
    }
}
