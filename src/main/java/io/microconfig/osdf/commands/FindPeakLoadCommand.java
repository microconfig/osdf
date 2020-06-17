package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.loadtesting.jmeter.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterMasterConfig;
import io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader;
import io.microconfig.osdf.loadtesting.jmeter.metrics.PeakMetrics;
import io.microconfig.osdf.loadtesting.jmeter.results.PeakResultsBuilder;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterDeployUtils.deployDeployments;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterDeployUtils.setSlavesHosts;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner.jmeterResourcesCleaner;
import static io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader.jmeterComponentsLoader;
import static io.microconfig.osdf.loadtesting.jmeter.metrics.PeakMetrics.peakMetrics;
import static io.microconfig.osdf.loadtesting.jmeter.metrics.PeakMetricsParser.peakMetricsParser;
import static io.microconfig.osdf.loadtesting.jmeter.metrics.loader.JmeterMetricsPuller.jmeterMetricsPuller;
import static io.microconfig.osdf.loadtesting.jmeter.results.PeakResultsBuilder.peakResultsBuilder;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class FindPeakLoadCommand {
    private final ClusterCLI cli;
    private final JmeterConfigProcessor configProcessor;

    public static FindPeakLoadCommand findPeakLoadCommand(ClusterCLI cli, JmeterConfigProcessor jmeterConfigProcessor) {
        return new FindPeakLoadCommand(cli, jmeterConfigProcessor);
    }

    public void run() {
        configProcessor.init();
        announce("Load components");
        JmeterComponentsLoader jmeterLoader = jmeterComponentsLoader(cli, configProcessor);
        List<JmeterComponent> slaveComponents = jmeterLoader.loadSlaves();
        JmeterComponent masterComponent = jmeterLoader.loadMaster();
        try (JmeterResourcesCleaner ignore = jmeterResourcesCleaner(jmeterLoader)) {
            deployDeployments(slaveComponents);
            setSlavesHosts(configProcessor, slaveComponents);
            findPeak(masterComponent, slaveComponents);
        }
    }

    private void findPeak(JmeterComponent masterComponent, List<JmeterComponent> slaveComponents) {
        PeakMetrics userLimits = peakMetricsParser(configProcessor.loadUserConfig()).parse();
        PeakMetrics currentMetrics = peakMetrics(userLimits.getMetricsFromConfig());
        int slaveNodesNumber = configProcessor.getSlaveConfigs().size();
        while (!userLimits.checkPeakResults(currentMetrics)) {
            currentMetrics.setStep(currentMetrics.getStep() + userLimits.getStep());
            announce("Check load. Number of users: " + currentMetrics.getStep() * slaveNodesNumber);
            redefineMasterAndDeploy(masterComponent, currentMetrics, slaveNodesNumber);
            jmeterMetricsPuller(configProcessor).load(currentMetrics, masterComponent);
            showResult(currentMetrics, currentMetrics);
            deleteResources(masterComponent, slaveComponents);
        }
        announce("The peak load between " + (currentMetrics.getStep() - userLimits.getStep()) * slaveNodesNumber
                + "-" + currentMetrics.getStep() * slaveNodesNumber + " users");
    }

    private void showResult(PeakMetrics userLimits, PeakMetrics currentMetrics) {
        PeakResultsBuilder builder = peakResultsBuilder(userLimits.getMetricsFromConfig(), currentMetrics);
        announce(builder.build());
    }

    private void redefineMasterAndDeploy(JmeterComponent masterComponent, PeakMetrics currentMetrics, int slaveNodesNumber) {
        JmeterMasterConfig masterConfig = configProcessor.getMasterConfig();
        masterConfig.setThreadGroupAttribute("ThreadGroup.num_threads", currentMetrics.getStep());
        masterConfig.setThreadGroupAttribute("ThreadGroup.duration", currentMetrics.getStep() * slaveNodesNumber);
        masterConfig.setJmeterPlanInMasterTemplateConfig();
        deployDeployments(masterComponent);
    }

    private void deleteResources(JmeterComponent masterComponent, List<JmeterComponent> slaveComponents) {
        masterComponent.deleteAll();
        slaveComponents.forEach(component -> component.pods().forEach(Pod::delete));
    }
}
