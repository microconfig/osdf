package io.microconfig.osdf.loadtesting.jmeter.metrics.loader;

import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.metrics.MetricsPuller;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;

import static io.microconfig.osdf.metrics.MetricsConfigParser.metricsConfigParser;
import static io.microconfig.osdf.utils.YamlUtils.getMap;

@Data
@RequiredArgsConstructor
public class PullerSettings {
    private final CountDownLatch countDownLatch;
    private final MetricsPuller puller;
    private final int delay;
    private final int timesToLoad;

    public static PullerSettings pullerSettings(CountDownLatch countDownLatch, JmeterConfigProcessor configProcessor, int timesToLoad) {
        int delay = configProcessor.getMasterConfig().getDuration() * 1000 / (timesToLoad + 1);
        MetricsPuller puller = metricsConfigParser().buildPuller(getMap(configProcessor.loadUserConfig(), "monitoring"));
        return new PullerSettings(countDownLatch, puller, delay, timesToLoad);
    }
}
