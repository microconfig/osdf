package io.microconfig.osdf.service.deployment.info;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.*;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
public class DefaultServiceDeploymentInfo implements ServiceDeploymentInfo {
    private final int replicas;
    private final int availableReplicas;
    private final String configVersion;
    private final String version;
    private final String hash;
    private final DeploymentStatus status;

    private DefaultServiceDeploymentInfo(int replicas, int available, int unavailable, String version,
                                         String configVersion, String hash) {
        this.replicas = replicas;
        this.availableReplicas = available;
        this.configVersion = configVersion;
        this.version = version;
        this.hash = hash;
        this.status = statusFromReplicas(replicas, available, unavailable);
    }

    public static DefaultServiceDeploymentInfo deploymentInfo(String name, String resourceKind, ClusterCLI cli) {
        List<String> lines = cli.execute("get " + resourceKind + " " + name + " -o custom-columns=" +
                "replicas:.spec.replicas," +
                "current:.status.replicas," +
                "available:.status.availableReplicas," +
                "unavailable:.status.unavailableReplicas," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion," +
                "configHash:.metadata.labels.configHash")
                .getOutputLines();
        if (lines.get(0).toLowerCase().contains("not found")) return of(NOT_FOUND);
        if (lines.size() < 2) throw new OSDFException("Error querying deployment info: " + lines);

        String[] fields = lines.get(1).split("\\s+");
        Integer replicas = castToInteger(fields[0]);
        Integer current = castToInteger(fields[1]);
        Integer available = castToInteger(fields[2]);
        Integer unavailable = castToInteger(fields[3]);
        String version = fields[4];
        String configVersion = fields[5];
        String hash = fields[6];
        if (replicas == null || current == null || available == null) return of(FAILED);
        return new DefaultServiceDeploymentInfo(replicas, available, unavailable == null ? 0 : unavailable, version, configVersion, hash);
    }

    private static DefaultServiceDeploymentInfo of(DeploymentStatus status) {
        return new DefaultServiceDeploymentInfo(0, 0,"?", "?", "?", status);
    }


    private DeploymentStatus statusFromReplicas(int replicas, int available, int unavailable) {
        DeploymentStatus status = FAILED;
        if (replicas == 0) {
            status = TURNED_OFF;
        } else if (replicas == available) {
            status = RUNNING;
        } else if (unavailable > 0) {
            status = NOT_READY;
        }
        return status;
    }

    @Override
    public int replicas() {
        return replicas;
    }

    @Override
    public int availableReplicas() {
        return availableReplicas;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String configVersion() {
        return configVersion;
    }

    @Override
    public String hash() {
        return hash;
    }

    @Override
    public DeploymentStatus status() {
        return status;
    }
}
