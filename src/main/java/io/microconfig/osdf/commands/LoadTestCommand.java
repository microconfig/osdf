package io.microconfig.osdf.commands;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.loadtesting.jmeter.JmeterComponent;
import io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult;
import io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader;
import io.microconfig.osdf.loadtesting.jmeter.loader.JmeterPathLoader;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.service.deployment.info.DeploymentStatus;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult.jmeterLogResult;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner.jmeterResourcesCleaner;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor.jmeterConfigProcessor;
import static io.microconfig.osdf.loadtesting.jmeter.loader.JmeterComponentsLoader.jmeterComponentsLoader;
import static io.microconfig.osdf.loadtesting.jmeter.loader.JmeterPathLoader.jmeterPathLoader;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.defaultServiceDeployPacksLoader;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class LoadTestCommand {
    private final OSDFPaths paths;
    private final ClusterCLI cli;
    private final Path jmeterPlanPath;
    private final String configName;
    private final int numberOfSlaves;

    public static LoadTestCommand loadTestCommand(OSDFPaths paths, ClusterCLI cli, String configName, int numberOfSlaves) {
        return new LoadTestCommand(paths, cli, null, configName, numberOfSlaves);
    }

    public static LoadTestCommand loadTestCommand(OSDFPaths paths, ClusterCLI cli, Path jmeterPlanPath, int numberOfSlaves) {
        return new LoadTestCommand(paths, cli, jmeterPlanPath, null, numberOfSlaves);
    }

    public void run() {
        JmeterPathLoader jmeterPathLoader = jmeterPathLoader(paths);
        Path jmeterComponentsPath = jmeterPathLoader.jmeterComponentsPathLoad();

        JmeterConfigProcessor jmeterConfigProcessor = jmeterPlanPath == null ?
                jmeterConfigProcessor(jmeterComponentsPath, numberOfSlaves, configName, getCurrentRoutesMap()) :
                jmeterConfigProcessor(jmeterComponentsPath, numberOfSlaves, jmeterPlanPath);
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
        int duration = jmeterConfigProcessor.getMasterConfig().getDuration(configName);
        announce("Start load testing. Duration: " + duration + " sec");
        JmeterLogResult jmeterChecker = jmeterLogResult(duration * 2, "... end of run", "summary");
        Pod pod = masterComponent.pods()
                .stream()
                .findFirst()
                .orElseThrow();
        announce(jmeterChecker.getResults(pod));
    }

    private Map<String, String> getCurrentRoutesMap() {
        return defaultServiceDeployPacksLoader(paths, cli).loadPacks()
                .stream()
                .filter(deployPack -> deployPack.deployment().info().availableReplicas() > 0)
                .filter(deployPack -> deployPack.deployment().info().status().equals(DeploymentStatus.RUNNING))
                .map(deployPack -> deployPack.deployment().name())
                .collect(Collectors.toMap(name -> name, this::getUserServiceRoutes));
    }

    private String getUserServiceRoutes(String name) {
        String command = "oc get route " + name + " -o custom-columns=HOST:.spec.host";
        List<String> output = cli.execute(command).getOutputLines();
        if (output.get(0).toLowerCase().contains("not found"))
            throw new OSDFException("Pod " + name + " ip not found");
        return output.get(1).strip();
    }
}
