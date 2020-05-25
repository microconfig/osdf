package io.microconfig.osdf.components;

import io.microconfig.osdf.components.info.DeploymentInfo;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.utils.Logger;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.components.properties.DeployProperties.deployProperties;
import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.osdf.openshift.Pod.fromOpenShiftNotation;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@Deprecated
public class DeploymentComponent extends AbstractOpenShiftComponent {
    private final boolean istioService;
    private boolean isPrimary = false;

    public DeploymentComponent(String name, String version, Path configDir, OpenShiftCLI oc) {
        super(name, version, configDir, oc);
        this.istioService = "istio".equals(deployProperties(configDir).getType());
    }

    public static DeploymentComponent component(String name, OSDFPaths paths, OpenShiftCLI oc) {
        DeploymentComponent component = componentsLoader(paths, of(name), oc)
                .load(DeploymentComponent.class)
                .stream()
                .findFirst()
                .orElse(null);
        if (component == null) throw new OSDFException("Component " + name + " not found");
        return component;
    }

    public static DeploymentComponent fromNotation(String notation, Path configDir, OpenShiftCLI oc) {
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

    public void deleteDeploymentConfig(String version) {
        String output = oc.execute("oc delete dc " + name + "." + version).getOutput();
        Logger.info(output);
    }

    public void stop() {
        scale(0);
    }


    public void restart() {
        int replicas = info().getReplicas();
        if (replicas > 0) {
            scale(0);
            scale(replicas);
        } else {
            upload();
        }
    }

    public List<Pod> pods() {
        return oc.execute("oc get pods " + label() + " -o name")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromOpenShiftNotation(notation, name, oc))
                .sorted()
                .collect(toUnmodifiableList());
    }

    public boolean isDeployed() {
        return !oc.execute("oc get dc " + fullName()).getOutput().contains("not found");
    }

    public boolean isRunning() {
        DeploymentInfo info = info();
        return info.getReplicas() == info.getAvailableReplicas() && info.getAvailableReplicas() > 0;
    }

    public DeploymentInfo info() {
        return DeploymentInfo.info(this, oc);
    }

    public List<DeploymentComponent> getDeployedComponents() {
        return oc.execute("oc get dc -l application=" + name + " -o name")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromNotation(notation, configDir, oc))
                .collect(toUnmodifiableList());
    }

    public boolean isPrimary() {
        if (!isIstioService()) return true;
        return isPrimary;
    }

    @Override
    public String fullName() {
        if (isPrimary()) return name;
        return super.fullName();
    }

    private void scale(int replicas) {
        oc.execute("oc scale dc " + fullName() + " --replicas=" + replicas)
                .throwExceptionIfError();
    }
}
