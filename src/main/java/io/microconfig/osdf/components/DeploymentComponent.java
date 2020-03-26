package io.microconfig.osdf.components;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.components.info.DeploymentInfo;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.utils.Logger;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.openshift.Pod.fromOpenShiftNotation;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;

public class DeploymentComponent extends AbstractOpenShiftComponent {
    public DeploymentComponent(String name, Path configDir, Path openShiftConfigDir, OCExecutor oc) {
        super(name, configDir, openShiftConfigDir, oc);
    }

    public static DeploymentComponent component(String name, Path componentsPath, OCExecutor oc) {
        DeploymentComponent component = componentsLoader(componentsPath, of(name), oc)
                .load(DeploymentComponent.class)
                .stream()
                .findFirst()
                .orElse(null);
        if (component == null) throw new RuntimeException("Component " + name + " not found");
        return component;
    }

    public void stop() {
        String output = oc.execute("oc scale dc " + name + " --replicas=0");
        Logger.info("oc: " + output);
    }

    public void restart() {
        stop();
        upload();
    }

    public List<Pod> pods() {
        return oc.executeAndReadLines("oc get pods --selector name=" + name + " -o name")
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromOpenShiftNotation(notation, name, oc))
                .sorted()
                .collect(toList());
    }

    public Pod pod(String podName) {
        return pod(pods(), podName);
    }

    public Pod pod(List<Pod> pods, String podName) {
        Integer order = castToInteger(podName);
        if (order != null) return pods.get(order);

        return pods.stream()
                .filter(pod -> pod.getName().equals(podName))
                .findFirst()
                .orElse(null);
    }

    public DeploymentInfo info(HealthChecker healthChecker) {
        return DeploymentInfo.info(this, oc, healthChecker);
    }
}
