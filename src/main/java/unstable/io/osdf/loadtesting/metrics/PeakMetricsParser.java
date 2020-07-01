package unstable.io.osdf.loadtesting.metrics;

import io.osdf.common.exceptions.PossibleBugException;
import unstable.io.osdf.metrics.Metric;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.loadtesting.testplan.utils.ParamUtils;
import unstable.io.osdf.metrics.MetricsConfigParser;

import java.util.Map;
import java.util.Set;

import static io.osdf.common.utils.YamlUtils.getMap;
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
            Set<Metric> metricSet = MetricsConfigParser.metricsConfigParser().fromYaml(getMap(userConfig, "monitoring"));
            return prepareJmeterParams(getMap(userConfig, "peak-load-test"), metricSet);
        }
        throw new PossibleBugException("'peak-load-test' params not defined in test config");
    }

    private PeakMetrics prepareJmeterParams(Map<String, Object> peakConfig, Set<Metric> metricSet) {
        PeakMetrics peakMetrics = PeakMetrics.peakMetrics(metricSet);
        peakMetrics.setStep(parseInt(ParamUtils.checkForNullAndReturn(peakConfig, "step")));
        if (peakConfig.containsKey("errors")) {
            peakMetrics.setErrors(parseDouble(ParamUtils.checkForNullAndReturn(peakConfig, "errors")));
        }
        if (peakConfig.containsKey("average-response-time")) {
            peakMetrics.setResponseTime(parseDouble(ParamUtils.checkForNullAndReturn(peakConfig, "average-response-time")));
        }
        return peakMetrics;
    }
}
