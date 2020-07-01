package unstable.io.osdf.loadtesting.metrics.loader;

import unstable.io.osdf.loadtesting.configs.JmeterConfigProcessor;
import unstable.io.osdf.metrics.MetricsPuller;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.metrics.MetricsConfigParser;

import java.util.concurrent.CountDownLatch;

import static io.osdf.common.utils.YamlUtils.getMap;

@Data
@RequiredArgsConstructor
public class PullerSettings {
    private final CountDownLatch countDownLatch;
    private final unstable.io.osdf.metrics.MetricsPuller puller;
    private final int delay;
    private final int timesToLoad;

    public static PullerSettings pullerSettings(CountDownLatch countDownLatch, JmeterConfigProcessor configProcessor, int timesToLoad) {
        int delay = configProcessor.getMasterConfig().getDuration() * 1000 / (timesToLoad + 1);
        MetricsPuller puller = MetricsConfigParser.metricsConfigParser().buildPuller(getMap(configProcessor.loadUserConfig(), "monitoring"));
        return new PullerSettings(countDownLatch, puller, delay, timesToLoad);
    }
}
