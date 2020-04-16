package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.components.properties.CanaryProperties;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.metrics.Metric;
import io.microconfig.osdf.metrics.MetricsPuller;
import io.microconfig.osdf.metrics.formats.MetricsParser;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.components.checker.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.microconfig.osdf.deployers.HiddenDeployer.hiddenDeployer;
import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.osdf.metrics.MetricsPuller.metricsPuller;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.*;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class CanaryDeployer implements Deployer {
    private final OCExecutor oc;
    private final MetricsParser metricsParser;
    private final HealthChecker healthChecker;

    public static CanaryDeployer canaryDeployer(OCExecutor oc, MetricsParser metricsParser, HealthChecker healthChecker) {
        return new CanaryDeployer(oc, metricsParser, healthChecker);
    }

    @Override
    public void deploy(DeploymentComponent component) {
        deployNewVersion(component);
        routeCanaryTraffic(component);
    }

    private void routeCanaryTraffic(DeploymentComponent component) {
        CanaryProperties canaryProperties = component.canaryProperties();

        int intervalInSec = canaryProperties.getIntervalInSec();
        int step = canaryProperties.getStep();
        List<Metric> metrics = canaryProperties.getMetrics();

        MetricsPuller metricsPuller = metricsPuller(metricsParser, canaryProperties.getUrl());
        Map<String, Double> initialMetrics = metricsPuller.pull();

        int currentTraffic = 0;
        while (currentTraffic < 100) {
            currentTraffic = min(currentTraffic + step, 100);
            virtualService(oc, component).setWeight(currentTraffic).upload();
            if (!waitAndMonitorMetrics(initialMetrics, metrics, metricsPuller, intervalInSec)) {
                virtualService(oc, component).setWeight(0).upload();
                announce("Removed traffic to new version");
                return;
            }
            announce("Current traffic: " + currentTraffic + "%");
        }
        announce("Successfully deployed new version");
    }

    private boolean waitAndMonitorMetrics(Map<String, Double> initialMetrics, List<Metric> metrics, MetricsPuller metricsPuller, int intervalInSec) {
        int time = 0;
        while (time < intervalInSec) {
            if (checkMetrics(initialMetrics, metrics, metricsPuller)) return false;
            sleepSec(1);
            time++;
        }
        return true;
    }

    private boolean checkMetrics(Map<String, Double> initialMetrics, List<Metric> metrics, MetricsPuller metricsPuller) {
        Map<String, Double> currentMetrics = metricsPuller.pull();

        List<Metric> badMetrics = metrics.stream()
                .filter(metric -> !metric.getType().check(
                        initialMetrics.get(metric.getKey()),
                        currentMetrics.get(metric.getKey()),
                        metric.getDeviation()
                ))
                .collect(toUnmodifiableList());
        if (!badMetrics.isEmpty()) {
            error("Bad metrics");
            badMetrics.forEach(metric -> error(metric.getKey() + ":" +
                    " expected - " + initialMetrics.get(metric.getKey()) +
                    " actual - " + currentMetrics.get(metric.getKey()))
            );
            return true;
        }
        return false;
    }

    private void deployNewVersion(DeploymentComponent component) {
        if (component.deployed()) {
            info("Component already deployed");
        } else {
            hiddenDeployer(oc).deploy(component);
        }
        if (!successfulDeploymentChecker(healthChecker).check(component)) throw new OSDFException("Deployment failed");
        info("Successfully deployed component");
    }
}
