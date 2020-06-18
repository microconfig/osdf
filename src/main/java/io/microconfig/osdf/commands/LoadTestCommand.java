package io.microconfig.osdf.commands;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.cluster.old.cluster.pod.Pod;
import io.microconfig.osdf.loadtesting.jmeter.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult;
import io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult.jmeterLogResult;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner.jmeterResourcesCleaner;
import static io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader.jmeterComponentsLoader;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class LoadTestCommand {
    private final ClusterCLI cli;
    private final JmeterConfigProcessor jmeterConfigProcessor;

    public static LoadTestCommand loadTestCommand(ClusterCLI cli, JmeterConfigProcessor jmeterConfigProcessor) {
        return new LoadTestCommand(cli, jmeterConfigProcessor);
    }

    public void run() {
        jmeterConfigProcessor.init();
        announce("Load components");
        JmeterComponentsLoader jmeterLoader = jmeterComponentsLoader(cli, jmeterConfigProcessor);
        List<JmeterComponent> slaveComponents = jmeterLoader.loadSlaves();
        JmeterComponent masterComponent = jmeterLoader.loadMaster();

        try (JmeterResourcesCleaner ignore = jmeterResourcesCleaner(jmeterLoader,
                jmeterConfigProcessor.getJmeterComponentsPath())) {
            deployDeployments(slaveComponents);
            setSlavesHosts(jmeterConfigProcessor, slaveComponents);
            deployDeployments(List.of(masterComponent));
            waitResults(jmeterConfigProcessor, masterComponent);
        }
    }

    private void setSlavesHosts(JmeterConfigProcessor jmeterConfigProcessor, List<JmeterComponent> slaveComponents) {
        List<String> slaveHosts = new ArrayList<>();
        slaveComponents.forEach(component -> slaveHosts.add(component.getServiceIp()));
        jmeterConfigProcessor.getMasterConfig().setHostsInMasterTemplateConfig(slaveHosts);
    }

    private void deployDeployments(List<JmeterComponent> components) {
        components.forEach(component -> {
            announce("Deploy " + component.getComponentName());
            component.deploy();
        });
    }

    private void waitResults(JmeterConfigProcessor jmeterConfigProcessor, JmeterComponent masterComponent) {
        int duration = jmeterConfigProcessor.getMasterConfig().getDuration(jmeterConfigProcessor.isJmxConfig());
        announce("Start load testing. Duration: " + duration + " sec");
        JmeterLogResult jmeterChecker = jmeterLogResult(duration * 2, "... end of run", "summary");
        Pod pod = masterComponent.pods()
                .stream()
                .findFirst()
                .orElseThrow();
        announce(jmeterChecker.getResults(pod));
    }
}
