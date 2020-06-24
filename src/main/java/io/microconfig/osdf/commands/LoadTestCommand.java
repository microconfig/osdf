package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.loadtesting.jmeter.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterDeployUtils.*;
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
        announce("Load components");
        JmeterComponentsLoader jmeterLoader = jmeterComponentsLoader(cli, jmeterConfigProcessor);
        List<JmeterComponent> slaveComponents = jmeterLoader.loadSlaves();
        JmeterComponent masterComponent = jmeterLoader.loadMaster();

        try (JmeterResourcesCleaner ignore = jmeterResourcesCleaner(jmeterLoader)) {
            deployDeployments(slaveComponents);
            setSlavesHosts(jmeterConfigProcessor, slaveComponents);
            deployDeployments(masterComponent);
            announce(waitResults(jmeterConfigProcessor, masterComponent));
        }
    }
}
