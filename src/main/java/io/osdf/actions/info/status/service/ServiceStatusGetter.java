package io.osdf.actions.info.status.service;

import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.osdf.actions.info.status.service.ServiceStatus.*;
import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class ServiceStatusGetter {
    private final ClusterCli cli;

    public static ServiceStatusGetter serviceStatusGetter(ClusterCli cli) {
        return new ServiceStatusGetter(cli);
    }

    public ServiceStatus statusOf(ServiceApplication service) {
        Optional<ClusterDeployment> deployment = service.deployment();
        if (deployment.isEmpty()) return NOT_FOUND;

        ClusterResource clusterResource = deployment.get().toResource();
        Optional<ResourceProperties> propertiesOptional = resourceProperties(cli, clusterResource,
                of("replicas", "spec.replicas",
                        "current", "status.replicas",
                        "available", "status.availableReplicas",
                        "unavailable", "status.unavailableReplicas",
                        "ready", "status.readyReplicas"));
        if (propertiesOptional.isEmpty()) return NOT_FOUND;

        ResourceProperties properties = propertiesOptional.get();
        Integer replicas = castToInteger(properties.get("replicas"));
        Integer current = castToInteger(properties.get("current"));
        Integer available = castToInteger(properties.get("available"));
        Integer unavailable = castToInteger(properties.get("unavailable"));
        Integer ready = castToInteger(properties.get("ready"));
        if (replicas == null) return FAILED;

        return chooseStatus(replicas, requireNonNullElse(current, 0), requireNonNullElse(available, 0),
                requireNonNullElse(unavailable, 0), requireNonNullElse(ready, 0));
    }

    private ServiceStatus chooseStatus(int replicas, int current, int available, int unavailable, int ready) {
        ServiceStatus status = FAILED;
        if (replicas == 0) {
            status = TURNED_OFF;
        } else if (current == ready) {
            status = READY;
        } else if (replicas == available) {
            status = RUNNING;
        } else if (unavailable > 0) {
            status = NOT_READY;
        }
        return status;
    }
}
