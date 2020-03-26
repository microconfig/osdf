package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.LogHealthChecker;
import io.microconfig.osdf.openshift.Pod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.info.DeploymentStatus.*;
import static io.microconfig.osdf.utils.CommandLineExecutor.executeAndReadLines;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Getter
@RequiredArgsConstructor
public class DeploymentInfo {
    private final int replicas;
    private final int availableReplicas;
    private final String configVersion;
    private final String projectVersion;
    private final List<Pod> pods;
    private final List<Boolean> podsHealth;
    private final DeploymentStatus status;

    private DeploymentInfo(DeploymentComponent component, LogHealthChecker logHealthChecker, int replicas, int available, int unavailable, String projectVersion, String configVersion) {
        this.replicas = replicas;
        this.availableReplicas = available;
        this.configVersion = configVersion;
        this.projectVersion = projectVersion;
        this.pods = component.pods();
        this.podsHealth = podsHealth(logHealthChecker, pods);
        boolean healthcheck = podsHealth.stream().allMatch(h -> h);
        this.status = status(healthcheck, replicas, available, unavailable);
    }

    public static DeploymentInfo info(DeploymentComponent component, LogHealthChecker logHealthChecker) {
        List<String> lines = executeAndReadLines("oc get dc " + component.getName() + " -o custom-columns=" +
                "replicas:.spec.replicas," +
                "current:.status.replicas," +
                "available:.status.availableReplicas," +
                "unavailable:.status.unavailableReplicas," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion", true);
        if (lines.get(0).toLowerCase().contains("not found")) return of(NOT_FOUND);

        String[] fields = lines.get(1).split("\\s+");
        Integer replicas = castToInteger(fields[0]);
        Integer current = castToInteger(fields[1]);
        Integer available = castToInteger(fields[2]);
        Integer unavailable = castToInteger(fields[3]);
        String projectVersion = fields[4];
        String configVersion = fields[5];
        if (replicas == null || current == null || available == null || unavailable == null) return of(UNKNOWN);
        return new DeploymentInfo(component, logHealthChecker, replicas, available, unavailable, projectVersion, configVersion);
    }

    private static DeploymentInfo of(DeploymentStatus status) {
        return new DeploymentInfo(0, 0, "?", "?", emptyList(), emptyList(), status);
    }

    private DeploymentStatus status(boolean healthcheck, Integer replicas, Integer available, Integer unavailable) {
        DeploymentStatus status = UNKNOWN;
        if (replicas == 0) {
            status = TURNED_OFF;
        } else if (unavailable > 0) {
            status = NOT_READY;
        } else if (replicas.equals(available)) {
            status = RUNNING;
        }
        if (status == RUNNING && !healthcheck) {
            status = BAD_HEALTHCHECK;
        }
        return status;
    }

    private List<Boolean> podsHealth(LogHealthChecker logHealthChecker, List<Pod> pods) {
        return pods.parallelStream().map(logHealthChecker::checkPod).collect(toList());
    }
}