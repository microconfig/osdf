package io.microconfig.osdf.loadtesting.jmeter.metrics;

import io.microconfig.osdf.exceptions.PossibleBugException;
import io.microconfig.osdf.metrics.Metric;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static io.microconfig.osdf.loadtesting.jmeter.metrics.PeakMetrics.peakMetrics;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ParamUtils.checkForNullAndReturn;
import static io.microconfig.osdf.metrics.MetricsConfigParser.metricsConfigParser;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

@RequiredArgsConstructor
public class PeakMetricsParser {
    private final Map<String, Object> userConfig;

    public static PeakMetricsParser peakMetricsParser(Map<String, Object> userConfig) {
        return new PeakMetricsParser(userConfig);
    }

    public PeakMetrics parse() {
        if (userConfig.containsKey("peak-load-test")) {
            Set<Metric> metricSet = metricsConfigParser().fromYaml(getMap(userConfig, "monitoring"));
            return prepareJmeterParams(getMap(userConfig, "peak-load-test"), metricSet);
        }
        throw new PossibleBugException("'peak-load-test' params not defined in test config");
    }

    private PeakMetrics prepareJmeterParams(Map<String, Object> peakConfig, Set<Metric> metricSet) {
        PeakMetrics peakMetrics = peakMetrics(metricSet);
        peakMetrics.setStep(parseInt(checkForNullAndReturn(peakConfig, "step")));
        if (peakConfig.containsKey("errors")) {
            peakMetrics.setErrors(parseDouble(checkForNullAndReturn(peakConfig, "errors")));
        }
        if (peakConfig.containsKey("average-response-time")) {
            peakMetrics.setResponseTime(parseDouble(checkForNullAndReturn(peakConfig, "average-response-time")));
        }
        return peakMetrics;
    }
}
