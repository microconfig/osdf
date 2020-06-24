package io.microconfig.osdf.loadtesting.jmeter.metrics.loader;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.exceptions.PossibleBugException;
import io.microconfig.osdf.loadtesting.jmeter.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.metrics.PeakMetrics;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterDeployUtils.waitResults;
import static io.microconfig.osdf.loadtesting.jmeter.metrics.loader.MetricsPuller.metricsPuller;
import static io.microconfig.osdf.loadtesting.jmeter.metrics.loader.PullerSettings.pullerSettings;
import static java.lang.Thread.currentThread;
import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;

@RequiredArgsConstructor
public class JmeterMetricsPuller {
    private final JmeterConfigProcessor configProcessor;
    private final CountDownLatch countDownLatch;
    private final List<Map<String, Double>> metrics;
    private final List<Thread> loaders;
    private static final int TIMES_TO_LOAD_METRICS_FOR_ONE_THREAD = 500;
    private static final int NUMBER_OF_THREADS = 7;

    public static JmeterMetricsPuller jmeterMetricsPuller(JmeterConfigProcessor configProcessor) {
        CountDownLatch countDownLatch = new CountDownLatch(NUMBER_OF_THREADS);
        List<Map<String, Double>> metrics = synchronizedList(new ArrayList<>());
        PullerSettings pullerSettings = pullerSettings(countDownLatch, configProcessor, TIMES_TO_LOAD_METRICS_FOR_ONE_THREAD);
        List<Thread> loaders = generate(() -> new Thread(metricsPuller(metrics, pullerSettings)))
                .limit(NUMBER_OF_THREADS)
                .collect(toList());
        return new JmeterMetricsPuller(configProcessor, countDownLatch, metrics, loaders);
    }

    public void load(PeakMetrics currentMetrics, JmeterComponent masterComponent) {
        try {
            loaders.forEach(Thread::start);
            String loadTestResults = waitResults(configProcessor, masterComponent);
            currentMetrics.setErrors(getInfoInTestResult(loadTestResults, "(", "%)"));
            currentMetrics.setResponseTime(getInfoInTestResult(loadTestResults, "Avg:", "Min:"));
            currentMetrics.setLoadRequests(getRequestLoadsFromResult(loadTestResults));
            countDownLatch.await();
            currentMetrics.setPulledMetrics(countAverageFromPulledMetrics(metrics));
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new OSDFException("InterruptedException while pulling metrics.");
        }
    }

    private Map<String, Double> countAverageFromPulledMetrics(List<Map<String, Double>> metrics) {
        return metrics.stream().reduce((firstMap, secondMap) ->
                Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum)))
                .orElseThrow(() -> new PossibleBugException("Couldn't find average of metrics."))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / metrics.size()));
    }

    private double getInfoInTestResult(String results, String open, String close) {
        return Arrays.stream(results.split("summary"))
                .reduce((first, second) -> second)
                .map(line -> substringBetween(line, open, close))
                .map(Double::parseDouble)
                .orElseThrow(() -> new PossibleBugException("Couldn't find information in load test results."));
    }

    private double getRequestLoadsFromResult(String results) {
        String[] summaries = results.split("summary");
        return Arrays.stream(summaries[summaries.length - 1].split("="))
                .reduce((first, second) -> second)
                .map(line -> substringBefore(line, "/s"))
                .map(Double::parseDouble)
                .orElseThrow(() -> new PossibleBugException("Couldn't find information in load test results."));
    }
}
