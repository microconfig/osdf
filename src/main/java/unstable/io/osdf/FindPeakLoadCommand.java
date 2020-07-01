package unstable.io.osdf;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.cluster.pod.Pod;
import unstable.io.osdf.loadtesting.JmeterComponent;
import unstable.io.osdf.loadtesting.JmeterResourcesCleaner;
import unstable.io.osdf.loadtesting.configs.JmeterConfigProcessor;
import unstable.io.osdf.loadtesting.configs.JmeterMasterConfig;
import unstable.io.osdf.loadtesting.loader.JmeterComponentsLoader;
import unstable.io.osdf.loadtesting.metrics.PeakMetrics;
import unstable.io.osdf.loadtesting.results.PeakResultsBuilder;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static unstable.io.osdf.loadtesting.JmeterDeployUtils.deployDeployments;
import static unstable.io.osdf.loadtesting.JmeterDeployUtils.setSlavesHosts;
import static unstable.io.osdf.loadtesting.JmeterResourcesCleaner.jmeterResourcesCleaner;
import static unstable.io.osdf.loadtesting.loader.JmeterComponentsLoader.jmeterComponentsLoader;
import static unstable.io.osdf.loadtesting.metrics.PeakMetrics.peakMetrics;
import static unstable.io.osdf.loadtesting.metrics.PeakMetricsParser.peakMetricsParser;
import static unstable.io.osdf.loadtesting.metrics.loader.JmeterMetricsPuller.jmeterMetricsPuller;
import static unstable.io.osdf.loadtesting.results.PeakResultsBuilder.peakResultsBuilder;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class FindPeakLoadCommand {
    private final ClusterCli cli;
    private final JmeterConfigProcessor configProcessor;

    public static FindPeakLoadCommand findPeakLoadCommand(ClusterCli cli, JmeterConfigProcessor jmeterConfigProcessor) {
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
