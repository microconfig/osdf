package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.info.DeploymentStatus.*;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@Getter
@RequiredArgsConstructor
public class DeploymentInfo {
    private final int replicas;
    private final int availableReplicas;
    private final String configVersion;
    private final String projectVersion;
    private final DeploymentStatus status;

    private DeploymentInfo(int replicas, int available, int unavailable, String projectVersion, String configVersion) {
        this.replicas = replicas;
        this.availableReplicas = available;
        this.configVersion = configVersion;
        this.projectVersion = projectVersion;
        this.status = status(replicas, available, unavailable);
    }

    public static DeploymentInfo info(DeploymentComponent component, OCExecutor oc) {
        List<String> lines = oc.execute("oc get dc " + component.getName() + "." + component.getVersion() + " -o custom-columns=" +
                "replicas:.spec.replicas," +
                "current:.status.replicas," +
                "available:.status.availableReplicas," +
                "unavailable:.status.unavailableReplicas," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion")
                .getOutputLines();
        if (lines.get(0).toLowerCase().contains("not found")) return of(NOT_FOUND);

        String[] fields = lines.get(1).split("\\s+");
        Integer replicas = castToInteger(fields[0]);
        Integer current = castToInteger(fields[1]);
        Integer available = castToInteger(fields[2]);
        Integer unavailable = castToInteger(fields[3]);
        String projectVersion = fields[4];
        String configVersion = fields[5];
        if (replicas == null || current == null || available == null || unavailable == null) return of(FAILED);
        return new DeploymentInfo(replicas, available, unavailable, projectVersion, configVersion);
    }

    private static DeploymentInfo of(DeploymentStatus status) {
        return new DeploymentInfo(0, 0,"?", "?", status);
    }

    private DeploymentStatus status(Integer replicas, Integer available, Integer unavailable) {
        DeploymentStatus status = FAILED;
        if (replicas == 0) {
            status = TURNED_OFF;
        } else if (replicas.equals(available)) {
            status = RUNNING;
        } else if (unavailable > 0) {
            status = NOT_READY;
        }
        return status;
    }
}