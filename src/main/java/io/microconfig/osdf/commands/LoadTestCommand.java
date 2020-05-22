package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.JmeterComponent;
import io.microconfig.osdf.components.loader.JmeterComponentsLoader;
import io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult;
import io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.components.loader.JmeterComponentsLoader.componentsLoader;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult.jmeterLogResult;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner.jmeterResourcesCleaner;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor.of;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class LoadTestCommand {
    private final OSDFPaths paths;
    private final Path jmeterPlanPath;
    private final int numberOfSlaves;
    private final OpenShiftCLI oc;

    public void run() {
        JmeterConfigProcessor jmeterConfigProcessor = of(oc, paths, numberOfSlaves, jmeterPlanPath);
        jmeterConfigProcessor.init();

        announce("Load components");
        JmeterComponentsLoader jmeterLoader = componentsLoader(jmeterConfigProcessor, oc);
        List<JmeterComponent> slaveComponents = jmeterLoader.loadSlaves();
        JmeterComponent masterComponent = jmeterLoader.loadMaster();

        try (JmeterResourcesCleaner ignore = jmeterResourcesCleaner(jmeterLoader)) {
            deployDeployments(slaveComponents);
            setSlavesHosts(jmeterConfigProcessor, slaveComponents);
            deployDeployments(List.of(masterComponent));
            waitResults(jmeterConfigProcessor, masterComponent);
        }
    }

    private void setSlavesHosts(JmeterConfigProcessor jmeterConfigProcessor, List<JmeterComponent> slaveComponents) {
        List<String> slaveHosts = new ArrayList<>();
        slaveComponents.forEach(component ->
                component.pods().forEach(pod -> slaveHosts.add(pod.getPodIp()))
        );
        jmeterConfigProcessor.getMasterConfig().setHostsInMasterTemplateConfig(slaveHosts);
    }

    private void deployDeployments(List<JmeterComponent> сomponents) {
        сomponents.forEach(component -> {
            announce("Deploy " + component.getComponentName());
            component.deploy();
        });
    }

    private void waitResults(JmeterConfigProcessor jmeterConfigProcessor, JmeterComponent masterComponent) {
        int duration = jmeterConfigProcessor.getMasterConfig().getDuration();
        announce("Start load testing. Duration: " + duration + " sec");
        JmeterLogResult jmeterChecker = jmeterLogResult(duration * 2, "... end of run", "summary =");
        Pod pod = masterComponent.pods()
                .stream()
                .findFirst()
                .orElseThrow();
        announce(jmeterChecker.getResults(pod));
    }
}
