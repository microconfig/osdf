package io.microconfig.osdf.components;

import io.microconfig.osdf.components.checker.LogHealthChecker;
import io.microconfig.osdf.components.info.DeploymentInfo;
import io.microconfig.osdf.components.info.PodsHealthcheckInfo;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.openshift.OpenShiftResource;
import io.microconfig.osdf.openshift.Pod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.checker.LogHealthChecker.logHealthChecker;
import static io.microconfig.osdf.components.info.DeploymentInfo.info;
import static io.microconfig.osdf.components.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.components.properties.DeployProperties.deployProperties;
import static io.microconfig.osdf.openshift.Pod.fromOpenShiftNotation;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.utils.Logger.announce;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@RequiredArgsConstructor
public class JmeterComponent {
    private final String componentName;
    private final String version;
    private final Path componentPath;
    private final OpenShiftCLI oc;

    public static JmeterComponent jmeterComponent(String componentName, Path componentPath, OpenShiftCLI oc) {
        return new JmeterComponent(componentName, deployProperties(componentPath).getVersion(), componentPath, oc);
    }

    public void deploy() {
        getLocalResources().forEach(openShiftResource -> {
            if (openShiftResource.isExists()) {
                openShiftResource.delete();
            }
            openShiftResource.upload();
        });
        if (!checkDeploy()) {
            announce(componentName + " hasn't been started. Please wait a cleaning resources.");
            throw new RuntimeException(componentName + " hasn't been started");
        }
    }

    public void deleteAll() {
        oc.execute("oc delete all -l application=" + componentName).throwExceptionIfError();
    }

    private List<OpenShiftResource> getLocalResources() {
        Path dir = of(componentPath + "/openshift");
        try (Stream<Path> list = list(dir)) {
            return list.filter(path -> path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))
                    .map(path -> OpenShiftResource.fromPath(path, oc))
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't open dir at " + dir, e);
        }
    }

    public boolean checkDeploy() {
        Integer podStartTime = deployProperties(componentPath).getPodStartTime();
        int currentTime = 0;
        while (!isRunning()) {
            currentTime++;
            if (currentTime > podStartTime) return false;
            sleepSec(1);
        }
        return info(componentName, oc).getStatus() == RUNNING && podsInfo().isHealthy();
    }

    public boolean isRunning() {
        DeploymentInfo info = info(componentName, oc);
        return info.getReplicas() == info.getAvailableReplicas() && info.getAvailableReplicas() > 0;
    }

    public PodsHealthcheckInfo podsInfo() {
        LogHealthChecker healthChecker = logHealthChecker(componentPath);
        List<Boolean> podsHealth = pods().parallelStream()
                .map(healthChecker::check)
                .collect(toUnmodifiableList());
        boolean healthy = podsHealth.stream().allMatch(t -> t);
        return new PodsHealthcheckInfo(pods(), podsHealth, healthy);
    }

    public List<Pod> pods() {
        return oc.execute("oc get pods " + label() + " -o name")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromOpenShiftNotation(notation, componentName, oc))
                .sorted()
                .collect(toUnmodifiableList());
    }

    protected String label() {
        return "-l \"application in (" + componentName + "), projectVersion in (" + version +  ")\"";
    }
}
