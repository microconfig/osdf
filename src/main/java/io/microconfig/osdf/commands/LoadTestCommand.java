package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.loader.JmeterComponentsLoader;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.components.checker.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.microconfig.osdf.components.loader.JmeterComponentsLoader.componentsLoader;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterResourcesCleaner.*;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor.of;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterLogResult.jmeterLogResult;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.osdf.resources.ResourceVersionInserter.resourceVersionInserter;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class LoadTestCommand {
    private final OSDFPaths paths;
    private final Path jmeterPlanPath;
    private final int numberOfSlaves;
    private final OCExecutor oc;
    private final Deployer deployer;

    public void run() {
        JmeterConfigProcessor jmeterConfigProcessor = of(oc, paths, numberOfSlaves, jmeterPlanPath);
        jmeterConfigProcessor.init();

        announce("Load components");
        JmeterComponentsLoader jmeterLoader = componentsLoader(jmeterConfigProcessor, oc);
        List<DeploymentComponent> slaveComponents = jmeterLoader.loadSlaves(DeploymentComponent.class);
        DeploymentComponent masterComponent = jmeterLoader.loadMaster(DeploymentComponent.class);

        try (OpenShiftProject ignored = create(paths, oc).connect();
             JmeterResourcesCleaner ignore = jmeterResourcesCleaner(jmeterLoader)) {
            deployDeployments(slaveComponents);
            setSlavesHosts(jmeterConfigProcessor, slaveComponents);
            deployDeployments(List.of(masterComponent));
            waitResults(jmeterConfigProcessor, masterComponent);
        }
    }

    private void setSlavesHosts(JmeterConfigProcessor jmeterConfigProcessor, List<DeploymentComponent> slaveComponents) {
        List<String> slaveHosts = new ArrayList<>();
        slaveComponents.forEach(component ->
                component.pods().forEach(pod -> slaveHosts.add(pod.getPodIp()))
        );
        jmeterConfigProcessor.getMasterConfig().setHostsInMasterTemplateConfig(slaveHosts);
    }

    private void deployDeployments(List<DeploymentComponent> slaveComponents) {
        slaveComponents.forEach(component -> {
            resourceVersionInserter(component.getConfigDir(), component.isPrimary() ? null : component.getVersion()).insert();
            announce("Deploy " + component.fullName());
            deployer.deploy(component);
            if (!successfulDeploymentChecker().check(component))
                throw new RuntimeException(component.fullName() + " hasn't been started");
        });
    }

    private void waitResults(JmeterConfigProcessor jmeterConfigProcessor, DeploymentComponent masterComponent) {
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
