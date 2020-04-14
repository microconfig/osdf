package io.microconfig.osdf.components;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.components.info.DeploymentInfo;
import io.microconfig.osdf.components.properties.CanaryProperties;
import io.microconfig.osdf.components.properties.DeployProperties;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.utils.Logger;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.osdf.openshift.Pod.fromOpenShiftNotation;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
public class DeploymentComponent extends AbstractOpenShiftComponent {
    private final boolean istioService;

    public DeploymentComponent(String name, String version, Path configDir, OCExecutor oc) {
        super(name, version, configDir, oc);
        this.istioService = "istio".equals(deployProperties().getType());
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

    public static DeploymentComponent fromNotation(String notation, Path configDir, OCExecutor oc) {
        String fullName = notation.split("/")[1];
        String name = fullName.split("\\.")[0];
        String version = fullName.substring(name.length() + 1);
        return new DeploymentComponent(name, version, configDir, oc);
    }


    @Override
    public void delete() {
        if (istioService) {
            virtualService(oc, this).deleteRules();
        }
        super.delete();
    }

    @Override
    public void deleteAll() {
        virtualService(oc, this).delete();
        super.deleteAll();
    }

    public void stop() {
        String output = oc.execute("oc scale dc " + fullName() + " --replicas=0");
        Logger.info("oc: " + output);
    }

    public void restart() {
        stop();
        upload();
    }

    public boolean deployed() {
        return !oc.execute("oc get dc " + fullName(), true).contains("not found");
    }

    public List<Pod> pods() {
        return oc.executeAndReadLines("oc get pods " + label() + " -o name")
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromOpenShiftNotation(notation, name, oc))
                .sorted()
                .collect(toUnmodifiableList());
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
                .orElseThrow(() -> new RuntimeException("Pod not found"));
    }

    public CanaryProperties canaryProperties() {
        return CanaryProperties.canaryProperties(configDir);
    }

    public DeployProperties deployProperties() {
        return DeployProperties.deployProperties(configDir);
    }

    public DeploymentInfo info(HealthChecker healthChecker) {
        return DeploymentInfo.info(this, oc, healthChecker);
    }

    public List<DeploymentComponent> getDeployedComponents() {
        return oc.executeAndReadLines("oc get dc -l application=" + name + " -o name")
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromNotation(notation, configDir, oc))
                .collect(toUnmodifiableList());
    }
}
